# Codex for Open Source — research notes + paste-ready application

Official form: https://openai.com/form/codex-for-oss/  
Repo: https://github.com/blueskyno6/java-maintainer-kit  
Program page: https://developers.openai.com/community/codex-for-oss

---

## 1) What public success cases actually show

Public X/Twitter threads with **verbatim approved form text** are scarce. What *is* documented:

### Confirmed / named high-signal recipients

| Signal | Evidence | Takeaway |
|---|---|---|
| **Albumentations** (Vladimir Iglovikov) | Blog: applied early March 2026; ~1 month later got “You’re in” + 6 months ChatGPT Pro. Application narrative used hard metrics: **15k+ stars**, **~140M downloads**, **~5–6M/month**, **~40k dependent repos**, papers, NumFOCUS affiliation. | Reviewers respond to **quantified adoption + ecosystem role**, not clever marketing copy. |
| **Bun / Ratatui / Apache Maven** | Named as Codex Open Source Fund recipients in community summaries | Credits go to **widely used infrastructure**, not greenfield demos. |
| **OpenAI community** | Maintainers report rolling review; **only selected applicants get email**; weeks of silence is normal; rejection often = no reply | Do not expect a “no”; silence ≠ pending forever. |

### What reviewers appear to weigh (official terms + successful narratives)

1. **Repository usage** — stars, downloads, dependents, Action installs  
2. **Ecosystem importance** — why other developers/projects rely on it  
3. **Active maintenance evidence** — releases, CI, issues/PRs, write access  
4. **Role** — Primary/Core maintainer (verifiable write access)  
5. **Concrete maintainer workflow** — PR review, triage, release automation (OpenAI’s own wording)  
6. **Credits ask scoped to automation** — PR bots / release workflows beat vague “I will code faster”

### What does *not* appear to work

- Brand-new **0-star / 0-download** repos created mainly to apply  
- Generic claims (“important to the ecosystem”) without numbers or dependents  
- Asking only for Pro with no maintainer-workload story  
- Treating this like a consumer promo instead of a **maintainer grant**

### Honest odds for this repo (today)

**Low if applied immediately.** As of the latest check: public repo ✅, CI green ✅, `v0.1.0` + `jmk-cli.jar` ✅, **stars ≈ 0**, **no Maven Central downloads yet**, **no external Action adopters yet**.

Best strategy:

1. **Optional early apply** with a strong ecosystem-gap narrative (OpenAI says “apply anyway and explain why”) — low probability, low cost.  
2. **Higher-probability apply** after 2–6 weeks of real signals (stars, Action usage in other repos, Reddit/HN mention, dependents).  
3. Keep the repo looking like an **active maintainer project** (releases, dogfooded CI, SECURITY/CONTRIBUTING).

---

## 2) Patterns to copy into the form (≤500 chars each)

- Lead with **what it does for maintainers** (PR review / release / triage).  
- Add **numbers** even if small, and label them honestly (`stars: 0 today`).  
- Name the **ecosystem niche** (Java/Maven multi-module review load).  
- For credits: **deterministic JMK analysis + LLM summary on top**, running in GitHub Actions.  
- Mention **dogfooding** (this repo runs JMK on itself).  
- Prefer **English** on the official form (OpenAI staff review in EN). Chinese below is for your drafting convenience.

---

## 3) Paste-ready form (English) — recommended

**GitHub username:** `blueskyno6`  
**GitHub repository URL:** `https://github.com/blueskyno6/java-maintainer-kit`  
**Role:** Primary maintainer  
**Interested in:** API credits for my project **and** Codex Security (optional; Security is selective)

### Why does this repository qualify? (EN, ≤500)

```
JMK is an Apache-2.0 CLI + GitHub Action for Java/Maven maintainer workflows: multi-module PR impact mapping, POM dependency diffs, risk signals, and Conventional Commits changelogs. It dogfoods itself in CI and targets OpenAI’s stated load—PR review, triage, releases—for JVM repos. Primary maintainer (write access). Public v0.1.0 + jmk-cli.jar. Stars/downloads early; fills a gap vs generic PR bots that ignore Maven reactors.
```

### How will you use API credits? (EN, ≤500)

```
Credits will power LLM-assisted PR review on top of JMK’s deterministic findings: summarize high-risk Java diffs, propose focused tests for impacted Maven modules, and draft release notes/changelogs. Runs in GitHub Actions for public OSS consumers of JMK—keeping module/dependency facts as source of truth while Codex reduces review latency for maintainers who currently triage reactors by hand.
```

### Anything else we should know? (EN, ≤500)

```
Stack: Java 17, Maven multi-module, tests, SECURITY.md, CONTRIBUTING, CI green, release v0.1.0. Roadmap: Gradle support, japicmp binary-compat checks, Maven Central. Happy to share Action run URLs and adoption metrics as they grow. Goal is durable maintainer automation for the Java ecosystem, not a one-off demo.
```

---

## 4) Paste-ready form (Chinese reference — translate/adapt; submit EN above)

### 为什么这个仓库符合要求？

```
Java Maintainer Kit（JMK）是 Apache-2.0 的 CLI + GitHub Action，专为 Java/Maven 维护者自动化：多模块 PR 影响面、POM 依赖 diff、风险信号、Conventional Commits changelog。本仓库在 CI 中自用（self-analyze + PR 评论），对准 OpenAI 描述的维护负荷：PR 审查、问题分诊、发版。我是 Primary maintainer（写权限）。已有公开 v0.1.0 与 jmk-cli.jar。星标/下载仍早期，但填补通用 PR bot 不懂 Maven reactor 的缺口。
```

### 你将如何使用 API credits？

```
Credits 用于在 JMK 确定性分析之上做 LLM 辅助 PR 审查：总结高风险 Java diff、为受影响 Maven 模块提出测试重点、起草 release notes。通过 GitHub Actions 服务使用 JMK 的公开仓库——模块/依赖事实以 JMK 为准，Codex 降低维护者手工梳理多模块变更的时间。
```

### 其他补充

```
技术栈：Java 17、Maven 多模块、单测、SECURITY/CONTRIBUTING、CI 通过、v0.1.0 已发布。路线图：Gradle、japicmp、Maven Central。可后续补充 Action 运行链接与采用数据。目标是可持续的 Java 维护自动化，而非一次性 demo。
```

---

## 5) Pre-submit checklist (this repo)

- [x] Profile / repo public  
- [x] Primary maintainer (owner)  
- [x] CI green on `main`  
- [x] Release `v0.1.0` with `jmk-cli.jar`  
- [ ] Stars / external Action adopters (still weak — improves odds a lot)  
- [ ] ChatGPT email = form email  
- [ ] OpenAI Organization ID (required if requesting API credits)  
- [ ] Prefer submitting the **English** drafts above  

---

## 6) After submit / if approved

1. Watch the ChatGPT-linked email for “You’re in” + Activate Pro.  
2. Activate on the **same** account; conflicting Plus subscriptions have broken activation for some maintainers (Albumentations case).  
3. API credits may be separate / not included even when Pro is offered — ask explicitly if credits matter.  
4. Silence for weeks is common; no rejection email is common.
