# Release Workflow

This document describes the release workflow for JUnit Jupiter DB Tester modules.

## Overview

The project supports two release methods:

1. **GitHub Actions (Recommended)**: Automated workflow with validation and approval gate
2. **Local Release**: Manual release from your development machine

### Released Modules

All modules share the same version and are released together:

| Module | Description |
|--------|-------------|
| [junit-jupiter-db-tester](../junit-jupiter-db-tester/) | Core library |
| [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) | Bill of Materials |
| [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) | Spring Boot Starter |

### Version Management

This project uses [axion-release-plugin](https://github.com/allegro/axion-release-plugin) for version management. Versions are derived from Git tags:

- **Tagged commit** (e.g., `v1.2.0`): Release version `1.2.0`
- **After tagged commit**: Snapshot version `1.2.1-SNAPSHOT`
- **No tags**: Default version `0.1.0-SNAPSHOT`

```bash
# Check current version
./gradlew currentVersion

# Example outputs:
# On tagged commit v1.2.0 → 1.2.0
# After v1.2.0 tag → 1.2.1-SNAPSHOT
```

---

## Option 1: GitHub Actions Release (Recommended)

The GitHub Actions workflow provides:
- Version validation (MAJOR.MINOR.PATCH format only)
- Automatic check that version is newer than existing tags
- Dry-run mode for testing before actual release
- Manual approval gate via GitHub Environments

### Prerequisites

See [PUBLISHING.md](PUBLISHING.md) for one-time setup:
- GitHub Environment (`maven-central`) with required reviewers
- GitHub Secrets (`GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`, `GPG_KEY_ID`, `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_TOKEN`)

### Release Process

**Step 1: Dry-Run (Recommended)**

1. Go to **Actions** → **Release** → **Run workflow**
2. Enter version (e.g., `1.2.0`)
3. Check **"Dry-run mode"**
4. Click **"Run workflow"**

This runs:
- Version format validation
- Check that version is newer than existing tags
- Build and test all modules

**Step 2: Actual Release**

1. Go to **Actions** → **Release** → **Run workflow**
2. Enter version (e.g., `1.2.0`)
3. Leave "Dry-run mode" **unchecked**
4. Click **"Run workflow"**
5. Wait for `validate` and `build-and-dry-run` jobs to complete
6. **Approve** the deployment in the `maven-central` environment
7. Wait for `release` job to complete

**Operations Performed:**
- Deploy all modules to Maven Central
- Create Git tag (`v1.2.0`) via axion-release-plugin
- Create GitHub Release with auto-generated release notes

### Verify Release

- **GitHub Release**: https://github.com/seijikohara/junit-jupiter-db-tester/releases
- **Maven Central** (10-30 minutes after release):
  - Core: https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester
  - BOM: https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-bom
  - Starter: https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-spring-boot-starter

---

## Option 2: Local Release

For local releases from your development machine.

### Prerequisites

See [PUBLISHING.md](PUBLISHING.md) for detailed setup instructions:
- Central Portal account and namespace verification
- API token generation
- GPG key setup
- Credential configuration in `~/.gradle/gradle.properties`

### Local Release Process

**Step 1: Check Current Version**

```bash
# View current version (derived from Git tags)
./gradlew currentVersion

# List existing tags
git tag -l 'v*' --sort=-v:refname
```

**Step 2: Build and Test**

Build with the release version (replace `1.2.0` with your target version):

```bash
./gradlew clean build -Prelease.forceVersion=1.2.0
```

**Step 3: Publish to Maven Central**

```bash
./gradlew publishAndReleaseToMavenCentral -Prelease.forceVersion=1.2.0 --no-configuration-cache
```

This uploads signed artifacts from all modules to Maven Central and automatically releases them.

**Note**: This can take several minutes for the deployment validation and publishing.

**Step 4: Create Git Tag**

Use axion-release-plugin to create and push the tag:

```bash
./gradlew createRelease -Prelease.version=1.2.0
```

Or manually:

```bash
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0
```

**Step 5: Create GitHub Release**

```bash
gh release create v1.2.0 --title "Release 1.2.0" --generate-notes
```

**Step 6: Verify Release**

**GitHub Release** (immediate):
- Visit: `https://github.com/seijikohara/junit-jupiter-db-tester/releases`
- Verify changelog and release notes

**Maven Central** (10-30 minutes):
- Core: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester`
- BOM: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-bom`
- Spring Boot Starter: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-spring-boot-starter`

**Test Download**:
```gradle
dependencies {
    // Using BOM (recommended)
    testImplementation platform('io.github.seijikohara:junit-jupiter-db-tester-bom:1.2.0')
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester'
    // Optional: Spring Boot integration
    testImplementation 'io.github.seijikohara:junit-jupiter-db-tester-spring-boot-starter'
}
```

---

## Changelog Generation

GitHub Release automatically generates changelogs based on merged PRs and commit history.

### Commit Message Format

Use **Conventional Commits** for better changelog organization:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### Commit Types

- `feat`: New feature (appears in changelog)
- `fix`: Bug fix (appears in changelog)
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `perf`: Performance improvements
- `test`: Adding or updating tests

### Examples

```bash
# Feature
git commit -m "feat: add support for PostgreSQL 17"

# Bug fix
git commit -m "fix: resolve null pointer exception in DatabaseAssertion"

# Breaking change
git commit -m "feat!: redesign API for better usability

BREAKING CHANGE: DatabaseTestExtension.getRegistry() now returns Optional<DataSourceRegistry>"
```

## Version Bumping Strategy

Version is determined manually following [Semantic Versioning](https://semver.org/):

- **PATCH** (1.0.0 → 1.0.1): Bug fixes, documentation updates
- **MINOR** (1.0.0 → 1.1.0): New features (backward compatible)
- **MAJOR** (1.0.0 → 2.0.0): Breaking changes

## Available Gradle Tasks

### Version Management (axion-release-plugin)

```bash
./gradlew currentVersion                              # Show current version
./gradlew currentVersion -Prelease.forceVersion=1.2.0 # Show forced version
./gradlew createRelease -Prelease.version=1.2.0       # Create and push tag
```

### Publishing

```bash
# Publish to Maven Central (all modules)
./gradlew publishAndReleaseToMavenCentral -Prelease.forceVersion=1.2.0 --no-configuration-cache

# Publish to local Maven repository (for testing)
./gradlew publishToMavenLocal -Prelease.forceVersion=1.2.0
```

## Troubleshooting

### Version Issues

```bash
# Check current version from Git tags
./gradlew currentVersion

# Fetch all tags if out of sync
git fetch --tags

# List tags
git tag -l 'v*' --sort=-v:refname
```

### GPG Signing Errors

**Error: "Inappropriate ioctl for device"**

This error occurs when GPG cannot prompt for passphrase in non-TTY environments.

**Solution**: Configure in-memory signing in `~/.gradle/gradle.properties`:

```properties
signing.keyId=YOUR_KEY_ID
signing.password=YOUR_GPG_PASSPHRASE
signing.secretKeyRingFile=/path/to/secring.gpg
```

Or use environment variables for CI:
```bash
export ORG_GRADLE_PROJECT_signingInMemoryKeyId=YOUR_KEY_ID
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=YOUR_PASSPHRASE
export ORG_GRADLE_PROJECT_signingInMemoryKey="$(gpg --armor --export-secret-keys YOUR_KEY_ID)"
```

### Maven Central Deployment Failed

- Check credentials in `~/.gradle/gradle.properties`:
  ```properties
  mavenCentralUsername=YOUR_USERNAME
  mavenCentralPassword=YOUR_TOKEN
  ```
- Verify GPG key is available: `gpg --list-keys`

For detailed troubleshooting, see [PUBLISHING.md](PUBLISHING.md).

## Best Practices

1. **Use Conventional Commits** for automatic changelog generation
2. **Test locally first** using `publishToMavenLocal`
3. **Use dry-run mode** in GitHub Actions before actual release
4. **Keep main branch clean** - avoid force pushes after tagging
5. **Document breaking changes** in commit messages and release notes
6. **Verify releases** on GitHub and Maven Central after publishing

## Example: Complete Release Workflow

```bash
# 1. Ensure all tests pass
./gradlew clean build test

# 2. Check current version and existing tags
./gradlew currentVersion
git tag -l 'v*' --sort=-v:refname | head -5

# 3. Publish to Maven Central (replace 1.2.0 with your version)
./gradlew publishAndReleaseToMavenCentral -Prelease.forceVersion=1.2.0 --no-configuration-cache

# 4. Create Git tag
./gradlew createRelease -Prelease.version=1.2.0

# 5. Create GitHub Release
gh release create v1.2.0 --title "Release 1.2.0" --generate-notes

# 6. Verify on GitHub and Maven Central
# GitHub: https://github.com/seijikohara/junit-jupiter-db-tester/releases
# Maven Central (Core): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester
# Maven Central (BOM): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-bom
# Maven Central (Starter): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-spring-boot-starter
```

## References

- [axion-release-plugin Documentation](https://axion-release-plugin.readthedocs.io/)
- [gradle-maven-publish-plugin Documentation](https://vanniktech.github.io/gradle-maven-publish-plugin/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
