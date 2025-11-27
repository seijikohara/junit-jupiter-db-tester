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
- GitHub Secrets (`GPG_PRIVATE_KEY`, `GPG_PASSPHRASE`, `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_TOKEN`)

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
- JReleaser dry-run (no actual deployment)

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
- Create Git tag (`v1.2.0`)
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
- Credential configuration
  - **Required**: `~/.gradle/gradle.properties` with `signing.gnupg.passphrase`
  - **Required**: `~/.jreleaser/config.properties` with Maven Central and GitHub credentials

### Version Management

#### How Versioning Works

The project version is managed via `gradle.properties`:

- **Development**: Default version is `0.1.0-SNAPSHOT`
- **Release**: Version is specified explicitly via command line (`-Pversion=1.0.0`) or GitHub Actions workflow

#### Check Current Version

```bash
# View version in gradle.properties
cat gradle.properties | grep version

# Or check via Gradle
./gradlew properties | grep "^version:"
```

### Local Release Process

**Step 1: Determine Release Version**

Check existing tags and determine the next version:

```bash
# List existing tags
git tag -l 'v*' --sort=-v:refname

# Determine next version based on semantic versioning:
# - PATCH (1.0.0 -> 1.0.1): Bug fixes
# - MINOR (1.0.0 -> 1.1.0): New features (backward compatible)
# - MAJOR (1.0.0 -> 2.0.0): Breaking changes
```

**Step 2: Build and Stage All Modules**

Build with the release version (replace `1.2.0` with your target version):

```bash
./gradlew clean build publish -Pversion=1.2.0 --no-configuration-cache
```

This generates for each module:
- Main JAR (or POM for BOM)
- Sources JAR (except BOM)
- Javadoc JAR (except BOM)
- POM file
- GPG signatures (`.asc` files)

Artifacts are staged in each module's `build/staging-deploy/` directory.

**Step 3: Deploy to Maven Central**

```bash
./gradlew jreleaserDeploy -Pversion=1.2.0 --no-configuration-cache
```

This uploads signed artifacts from all modules to Maven Central.

**Note**: This can take 30-45 minutes for the deployment validation and publishing.

**Step 4: Create Git Tag and GitHub Release**

```bash
# Create and push tag
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0

# Create GitHub Release (using gh CLI)
gh release create v1.2.0 --title "Release 1.2.0" --generate-notes
```

**Step 5: Verify Release**

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

#### Dry Run (Testing)

Test the release process without actually deploying:

```bash
./gradlew jreleaserDeploy -Pversion=1.2.0 --dryrun --no-configuration-cache
```

---

## Changelog Generation

JReleaser automatically generates changelogs based on **Conventional Commits**.

### Commit Message Format

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

### Pre-release Versions

For pre-release versions, use appropriate suffixes:

```bash
# Beta release
./gradlew clean build publish -Pversion=1.0.0-beta.1

# Release candidate
./gradlew clean build publish -Pversion=1.0.0-rc.1
```

## Available Gradle Tasks

### Version Check

```bash
./gradlew properties | grep "^version:"    # Show current version
```

### Publishing (Per Module)

```bash
# Core library
./gradlew :junit-jupiter-db-tester:publishToMavenLocal                       # Test locally
./gradlew :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository # Stage artifacts

# BOM
./gradlew :junit-jupiter-db-tester-bom:publishToMavenLocal
./gradlew :junit-jupiter-db-tester-bom:publishAllPublicationsToStagingRepository

# Spring Boot Starter
./gradlew :junit-jupiter-db-tester-spring-boot-starter:publishToMavenLocal
./gradlew :junit-jupiter-db-tester-spring-boot-starter:publishAllPublicationsToStagingRepository
```

### JReleaser

```bash
./gradlew :junit-jupiter-db-tester:jreleaserConfig        # Show configuration
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease   # GitHub Release + Maven Central (recommended)
./gradlew :junit-jupiter-db-tester:jreleaserRelease       # GitHub Release only
./gradlew :junit-jupiter-db-tester:jreleaserDeploy        # Maven Central only
```

## Troubleshooting

### Version Issues

```bash
# Check Git tags
git tag -l 'v*' --sort=-v:refname

# Fetch all tags if out of sync
git fetch --tags

# Check current version in gradle.properties
cat gradle.properties | grep version
```

### GPG Signing Errors

**Error: "Inappropriate ioctl for device"**

This error occurs when GPG cannot prompt for passphrase in non-TTY environments.

**Solution**: Add GPG passphrase to `~/.gradle/gradle.properties`:

```properties
signing.gnupg.passphrase=your-gpg-passphrase
```

### GitHub Release Failed

**Error: "403: Forbidden - Resource not accessible by personal access token"**

This occurs when the GitHub token lacks necessary permissions for JReleaser to create releases.

**Solution**: Ensure your GitHub Personal Access Token has the required permissions:
- Token needs `contents: write` permission (or `repo` scope for classic tokens)
- Create token at: https://github.com/settings/tokens
- Update `JRELEASER_GITHUB_TOKEN` in `~/.jreleaser/config.properties`

**Workaround** (if token permissions cannot be updated):
Use `gh` CLI to create releases instead of `jreleaserFullRelease`:
```bash
# Deploy to Maven Central only
./gradlew :junit-jupiter-db-tester:jreleaserDeploy --no-configuration-cache

# Create GitHub Release manually
gh release create v1.0.0 \
  --title "v1.0.0" \
  --notes-file junit-jupiter-db-tester/build/jreleaser/release/CHANGELOG.md
```

### Maven Central Deployment Timeout

**Warning**: "Deployment timeout exceeded. However, the remote operation may still be successful."

This is normal for initial deployments, which can take 30-45 minutes. The deployment continues in the background.

**Verification**:
1. Log in to [Central Portal](https://central.sonatype.com/publishing)
2. Check deployment status (look for "PUBLISHED" status)
3. Wait for Maven Central replication (additional 10-30 minutes)

### Maven Central Deployment Failed

- Check credentials in `~/.jreleaser/config.properties`
- Verify GPG key is available: `gpg --list-keys`
- Check staging repositories:
  ```bash
  ls -la junit-jupiter-db-tester/build/staging-deploy/
  ls -la junit-jupiter-db-tester-bom/build/staging-deploy/
  ls -la junit-jupiter-db-tester-spring-boot-starter/build/staging-deploy/
  ```
- Review JReleaser logs: `junit-jupiter-db-tester/build/jreleaser/trace.log`

For detailed troubleshooting, see [PUBLISHING.md](PUBLISHING.md).

## Best Practices

1. **Use Conventional Commits** for automatic changelog generation
2. **Test locally first** using `publishToMavenLocal` and `--dry-run`
3. **Create pre-release versions** for testing before major releases
4. **Keep main branch clean** - avoid force pushes after tagging
5. **Document breaking changes** in commit messages and release notes
6. **Verify releases** on GitHub and Maven Central after publishing

## Example: Complete Release Workflow

```bash
# 1. Ensure all tests pass
./gradlew clean build test

# 2. Check existing tags and determine next version
git tag -l 'v*' --sort=-v:refname | head -5
# Determine version based on changes (PATCH/MINOR/MAJOR)

# 3. Build and publish all modules to staging (replace 1.2.0 with your version)
./gradlew clean build publish -Pversion=1.2.0 --no-configuration-cache

# 4. Deploy to Maven Central
./gradlew jreleaserDeploy -Pversion=1.2.0 --no-configuration-cache

# 5. Create Git tag and GitHub Release
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0
gh release create v1.2.0 --title "Release 1.2.0" --generate-notes

# 6. Verify on GitHub and Maven Central
# GitHub: https://github.com/seijikohara/junit-jupiter-db-tester/releases
# Maven Central (Core): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester
# Maven Central (BOM): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-bom
# Maven Central (Starter): https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester-spring-boot-starter
```

## References

- [JReleaser Documentation](https://jreleaser.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
