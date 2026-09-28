# AGENTS.md — weaponmod 开发约束文档

本文件约束在此仓库中工作的一切 AI 代理（与人）。修改代码前请先通读本文，尤其是「必须遵守的模式」与「已知的坑」两节。

## 1. 项目概述

Minecraft 1.21.1 武器扩展模组，modid 为 `weaponmod`。为游戏添加多把带技能/充能机制的自定义武器、药水效果、弹射物实体与粒子。

| 项 | 值 |
|---|---|
| Minecraft | 1.21.1（`minecraft_version`） |
| 加载器 | NeoForge `21.1.222`（NeoGradle userdev `7.0.189`） |
| Java | 21（toolchain 强制，编译编码 UTF-8） |
| 核心前置依赖 | GeckoLib `4.7.5.1`（动画与模型渲染，31+ 个类在用） |
| modid | `weaponmod` |
| 根包 | `com.example.weaponmod` |
| 主类 | `WeaponMod.java`（`@Mod(WeaponMod.MODID)`） |
| Mixin 配置 | `src/main/resources/weaponmod.mixins.json` |

README.md 目前仍是未填写的模板（占位符「模组名称」等），**不要**以 README 为行为依据；以代码为准。

## 2. 构建与运行

```bash
./gradlew build        # 编译 + 打 jar（产出 build/libs/weaponmod-*.jar）
./gradlew runClient    # 启动客户端测试
./gradlew runServer    # 启动专用服务器（--nogui）
./gradlew runData      # 数据生成，输出到 src/generated/resources/
./gradlew runGameTestServer
```

- 开发分支为 `dev`，主分支为 `main`；常规改动在 `dev` 上进行，经 PR 合入 `main`。
- 提交前必须保证 `./gradlew build` 通过（含编译与资源处理）。
- 不要提交：`build/`、`runs/`、`.gradle/`、`.idea/`、`.local-maven-repo/`、`.tmp/`（已在 .gitignore）。
- `gradle.properties` 中 `mod_group_id` 仍是 `com.example.examplemod`，与实际包名不一致——若发布前修正，需同步 build.gradle 的发布配置。

## 3. 包结构（src/main/java/com/example/weaponmod/）

| 包 | 职责 |
|---|---|
| `WeaponMod.java` | 主类。集中调用各 `DeferredRegister` 的 register；`RegisterPayloadHandlersEvent` 中注册网络包 |
| `items/` | `ModItems` 注册表 + `custom/` 武器类 + `model/`、`renderer/`（GeckoLib） |
| `weaponskill/` | 武器技能系统：`SkillManager`（V 技能范围伤害+斩杀）、`DashManager`（激流式冲刺）、`ChargeManager`（充能） |
| `attachments/` | `ModAttachments`：每把带技能武器一个 Integer 充能 attachment（serialize + sync + copyOnDeath） |
| `events/` | `ServerTickHandler`（技能/冲刺/充能的总驱动）、`EventHandler`（受伤事件，诅咒刀）、`ClientKeyHandler`（V 键）、`ClientModEvents` |
| `network/` | 自定义 payload（C2S）。当前仅 `NullBladeSkillPacket` |
| `entities/` | 弹射物/特效实体（`BlackFireBallEntity`、`NullBladeDash`）+ model/renderer |
| `effects/`、`potions/` | 药水效果（`ShieldEffect` 护盾用 `getPersistentData()` NBT 存值，key 见 `ShieldData`） |
| `particles/`、`pojo/` | 粒子类型与 `ParticleOption`（如 `NullBladeParticleOption` 把旋转角编码进 option 而非 speed/dx 参数） |
| `mixin/`、`mixin/client/` | `LivingEntityAccessor`（访问 autoSpinAttack 私有字段）与客户端渲染 mixin |
| `ui/` | 创造模式标签页 `ModTabs`、`ShieldHudRenderer` |
| `sounds/` | `ModSounds` 音效注册 |

资源根：`src/main/resources/assets/weaponmod/`（`geo/`、`animations/`、`models/`、`textures/`、`lang/`、`sounds/`、`particles/`）。

## 4. 必须遵守的模式

### 4.1 注册
- 一切注册对象走 `DeferredRegister` / `DeferredItem`，在主类构造器中统一 `register(modEventBus)`。禁止在静态初始化块里直接注册或提前 `get()`。
- 注册表对象未冻结前不得 `get()`（只在运行期事件/实体逻辑中解引用）。

### 4.2 武器类模板
新武器一律遵循现有七把武器的结构（参考 `NullBlade`）：
1. `extends SwordItem implements GeoItem`（或相应基类）；
2. 声明 `public static final ResourceLocation WEAPON_ID`（命名空间必须是 `weaponmod`）与 `public static final int MAX_CHARGE`；
3. 技能入口写成 `public static void activateSkill(Player player)`，内部第一行拦截 `level.isClientSide()`，即**技能逻辑只在服务端执行**；
4. `createGeoRenderer(...)` 返回对应 `XxxRenderer`；配套 `XxxModel`、`assets/weaponmod/geo/*.geo.json` 与 `animations/*.animation.json`。

### 4.3 客户端/服务端边界（最重要的一条约束）
- 一切游戏逻辑（伤害、充能、实体生成、粒子发送）**服务端权威**。客户端只负责：按键输入 → `PacketDistributor.sendToServer(...)` 发包。
- C2S 包写在 `network/`，在主类 `onRegisterPayloads` 中 `registrar.playToServer(...)` 注册；handler 内用 `context.enqueueWork` 并校验 `context.player() instanceof ServerPlayer`。
- 新增只与客户端相关的类（渲染、HUD、按键）必须挂在 `@EventBusSubscriber(value = Dist.CLIENT)` 下（见 `ClientKeyHandler`、`ClientModEvents`）。

### 4.4 Tick 驱动
持续型机制（技能多段、冲刺、被动充能）不要各自挂监听：统一由 `ServerTickHandler.onServerTick(LevelTickEvent.Post)` 调用对应 Manager 的 `tick(ServerLevel)`；Manager 内部用 `Map<UUID, Data>` + 迭代器删除维护状态，必须校验玩家在线与维度一致（照抄 `SkillManager` / `DashManager` 的写法）。

### 4.5 无敌帧绕过
「无视无敌帧补刀」是本模组反复出现的惯用写法，必须保持这个顺序，不要改成其他实现：
```java
int original = target.invulnerableTime;
target.invulnerableTime = 0;
target.hurt(source, damage);
target.invulnerableTime = original;
```

### 4.6 充能系统
- 充能值存在玩家 attachment 上，用 `ChargeManager` 的 API（`addCharge` / `getCharge` / `tryConsumeFull`），不要直接 `player.setData`。
- 每把武器：注册名 ↔ attachment ↔ `ChargeManager` 里两处 `switch`（`getAttachment` 与 `getMaxCharge`）——**新增充能武器必须同时补全这两处 switch**，否则充能静默失效。
- 修改充能后必须 `player.syncData(attachment)` 同步到客户端。

### 4.7 文本与本地化
- 所有玩家可见文本用 `Component.translatable(key)`；key 写进 `assets/weaponmod/lang/zh_cn.json` 与 `en_us.json`，两份都要加，zh_cn 为主。
- 武器名/提示大量使用 `§` 颜色码，保持现有风格。

### 4.8 Mixin
- 只为无法通过事件/AT 实现的功能加 mixin；新增成员方法统一 `weaponmod$` 前缀（见 `LivingEntityAccessor`）。
- 新 mixin 文件必须同步登记进 `weaponmod.mixins.json`（公共段 `mixins` / 客户端段 `client`）。
- 优先考虑 NeoForge 事件（如 `LivingIncomingDamageEvent`）而非 mixin。

### 4.9 资源
- GeckoLib 动画资产的命名与注册名一致（如 `null_katana.geo.json`），不是类名。
- lang 中存在无 lang 前缀的 `entity.weaponmod.test_monster` 等遗留 key；不要引用不存在的 key，也不要随手清理可能被其他 lang 文件引用的 key 之外的条目。

### 4.10 临时文件与解包源码
- 从 jar/依赖中解包或反编译出来、仅供阅读参考的源码文件，一律统一放在仓库根目录的 `.tmp/` 目录内，不要散落在仓库其他位置（历史上曾把 NeoForge 源码直接放在根目录 `net/` 下，已清理，勿再犯）。
- `.tmp/` 已被 .gitignore 忽略，**永远不允许**用 git 追踪它：不要 `git add -f`、不要写进任何提交。
- `.tmp/` 里的内容视为可随时删除的缓存，不要在正式代码、构建脚本或文档中引用其路径。

## 5. 注册名 ↔ 类名映射（易错点）

**注册名（物品 id / 资产文件名 / attachment 派生名）与 Java 类名不一致**，查询资产或写 `switch` 时必须按下表：

| 物品注册名（modid 下） | 类名 | 中文名 | MAX_CHARGE |
|---|---|---|---|
| `null_katana` | `NullBlade` | 空 | 450 |
| `moon_marrow_scythe` | `MoonMarrowScythe` | 月髓 | 300 |
| `antares_rapier` | `AntaresRapier` | 流火之熄 | 400 |
| `azure_mountains_masher_sword` | `AzureMountainsMasher` | 苍螭破岳剑 | — |
| `great_apple_heavy_axe` | `GreatApple` | 大苹果 | — |
| `perpetual_nightstar_trident` | `PerpetualNightStar` | 永夜飞星 | — |
| `cyanfrost_violetvolt_katana` | `CyanFrostVioletVolt` | 青霜紫电 | 400 |
| `naginata_sword` | `NaginataSword` | 十字刀（诅咒刀：持有时受击即死，勿"修复"此行为） | — |
| `healing_scroll` | `HealingScroll` | 治疗卷轴 | — |
| `test_sword` | `TestSword` | 测试之剑 | — |
| `ruby` / `raw_ruby` | — | 大红玉 / 红玉（材料） | — |

注意：`NullBlade.WEAPON_ID` 是 `weaponmod:null_blade`，而物品注册名是 `null_katana`——`ChargeManager` 的 switch 匹配的是 `null_blade`。改动任一侧前先全局搜索确认引用。

## 6. 新增一把带技能武器的 Checklist

1. `items/custom/` 新建武器类（按 4.2 模板）；
2. `ModItems` 注册（起注册名，注意 4.2 与第 5 节的命名一致性）；
3. 如有充能：`ModAttachments` 加 attachment + `ChargeManager` 两处 switch 补全 + `tickPassive` 加一行被动充能；
4. 技能逻辑放 `weaponskill/` 对应 Manager 或武器类静态方法，并在 `ServerTickHandler` 挂接（如需逐 tick）；
5. 如需按键触发：复用/扩展 `ClientKeyHandler` + 新建 C2S packet + 主类注册 payload；
6. GeckoLib：`items/model/XxxModel`、`items/renderer/XxxRenderer`、`geo/xxx.geo.json`、`animations/xxx.animation.json`、`models/item/xxx.json`、`textures/item/xxx.png`；
7. `zh_cn.json` 与 `en_us.json` 补全名称、tooltip、消息 key；
8. `./gradlew build` 通过后进游戏 `runClient` 实测。

## 7. 已知的坑

- `effects/cutsom/` 包名拼写错误（custom → cutsom）。**为保持 diff 最小暂不重命名**；新效果类继续放这个包，除非你获得明确授权做全量重构。
- `GreatApple` 类名下是重斧（物品名「大苹果」）、`PerpetualNightStar` 是三叉戟类——不要按类名猜测武器形态。
- README.md、`mod_license`（ARR）与 `TEMPLATE_LICENSE.txt`（MIT）互相矛盾；改许可前先问维护者，不要自行统一。
- `SkillManager` 等使用无界静态 `HashMap` 存玩家状态；并发模型依赖主线程 tick，勿在其他线程访问。
- 伤害数值（如 12.0f × 6 段、斩杀阈值 50/100）是策划数值，不要在重构时"顺手"调整。

## 8. 验证要求

任何改动的最低验收标准：
1. `./gradlew build` 零错误；
2. 涉及游戏逻辑的改动必须在 `runClient` 中实测（服务端逻辑用局域网或 `runServer` 验证）；
3. 涉及同步（attachment、packet）的改动，验证单人 + 断线重连后数值正确（attachment 已 `copyOnDeath`，死亡不掉充能）；
4. 新增/修改文本后检查两种语言文件 JSON 语法合法。
