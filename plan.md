# Java Upgrade Plan

## Technology Stack

| Component | Current Version | Target Version | Compatibility | Action |
|-----------|-----------------|----------------|---------------|--------|
| Java | 17 | 21 | Compatible | Upgrade |
| Spring Boot | 3.2.3 | 3.2.3 | Compatible | No change |
| Dependencies | Various | Compatible | No change |

## Available Tools

- JDK 17: Installed at C:\Program Files\Java\jdk-17\bin
- JDK 21: To be installed using install_jdk tool
- Maven: Wrapper available (mvnw.cmd)

## Key Challenges

Low risk upgrade as Spring Boot 3.2.3 supports Java 21, and all dependencies are compatible.

## Upgrade Steps

1. **Setup Environment**
   - Install JDK 21.

2. **Setup Baseline**
   - Stash uncommitted changes (if git available).
   - Run compile and test with current JDK 17, document results.

3. **Upgrade JDK**
   - Update `pom.xml` `java.version` property to 21.
   - Set environment to use JDK 21.
   - Run compile and test with JDK 21.

4. **Final Validation**
   - Verify all goals met, tests pass.
   - Achieve upgrade success criteria.

## RULES

- For incompatible deps: Prefer Replacement > Adaptation > Rewrite (none needed here).
- Intermediate versions: None required, direct upgrade possible.
- Step sequence: Environment setup, baseline, upgrade steps in dependency order, final validation.

## Plan Review

No unfixable limitations identified. The plan is complete and feasible.
