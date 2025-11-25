# Release Workflow

This document describes the automated release workflow for `junit-jupiter-db-tester` using axion-release-plugin and JReleaser.

## Overview

The project uses a fully automated release workflow:

1. **Version Management**: axion-release-plugin manages versions based on Git tags
2. **GitHub Release**: JReleaser creates GitHub releases with auto-generated changelogs
3. **Maven Central**: JReleaser publishes artifacts to Maven Central

## Prerequisites

See [PUBLISHING.md](PUBLISHING.md) for detailed setup instructions:
- Central Portal account and namespace verification
- API token generation
- GPG key setup
- Credential configuration
  - **Required**: `~/.gradle/gradle.properties` with `signing.gnupg.passphrase`
  - **Required**: `~/.jreleaser/config.properties` with Maven Central and GitHub credentials

## Version Management

### How Versioning Works

The project version is automatically derived from Git tags:

- **No tags**: Version is `0.1.0-SNAPSHOT`
- **Tagged commit**: Version is the tag (e.g., `v1.0.0` → `1.0.0`)
- **Commit after tag**: Version is `<next-version>-SNAPSHOT` (e.g., `1.0.1-SNAPSHOT`)

### Check Current Version

```bash
./gradlew currentVersion
# Output: Project version: 1.0.0-SNAPSHOT
```

### Version Commands

```bash
./gradlew currentVersion                            # Show current version
./gradlew verifyRelease                             # Verify release configuration
./gradlew release                                   # Create release tag
./gradlew release -Prelease.version=2.0.0          # Create specific version tag
./gradlew markNextVersion -Prelease.version=2.0.0  # Mark next version
```

## Release Process

### Option 1: Manual Release (Recommended for First Release)

**Step 1: Create Release Tag**

```bash
# Create release tag (this will create v1.0.0 and push to remote)
./gradlew release
# Or specify version: ./gradlew release -Prelease.version=1.0.0
```

**Step 2: Build and Stage Artifacts**

```bash
./gradlew :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          --no-configuration-cache
```

**Step 3: Deploy to GitHub and Maven Central**

```bash
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --no-configuration-cache
```

This will:
- ✅ Create GitHub Release with auto-generated changelog
- ✅ Upload signed artifacts to Maven Central
- ✅ Automatically publish to Maven Central (no manual approval!)

**Note**: This can take 30-45 minutes for the initial deployment validation and publishing.

**Step 4: Verify Release**

**GitHub Release** (immediate):
- Visit: `https://github.com/seijikohara/junit-jupiter-db-tester/releases`
- Verify changelog and release notes

**Maven Central** (10-30 minutes):
- Check: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester`
- Verify version, POM metadata, and signatures

**Test Download**:
```gradle
dependencies {
    testImplementation("io.github.seijikohara:junit-jupiter-db-tester:1.0.0")
}
```

**Step 5: Update to Next Development Version**

The version automatically becomes `1.0.1-SNAPSHOT` after the tag is created. No manual version updates needed!

```bash
./gradlew currentVersion
# Output: Project version: 1.0.1-SNAPSHOT
```

### Option 2: Automated Release (One Command)

```bash
./gradlew release \
          :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester:jreleaserFullRelease \
          --no-configuration-cache
```

⚠️ Use this only after successfully completing at least one manual release.

### Option 3: Dry Run (Testing)

```bash
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --dry-run --no-configuration-cache
```

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

### Automatic Version Bumping

axion-release-plugin calculates the next version based on the current tag:

- Patch bump (default): `1.0.0` → `1.0.1`
- Minor bump: `./gradlew release -Prelease.versionIncrementer=incrementMinor`
- Major bump: `./gradlew release -Prelease.versionIncrementer=incrementMajor`

### Manual Version Bumping

```bash
./gradlew release -Prelease.version=2.0.0
```

### Pre-release Versions

```bash
# Beta release
./gradlew release -Prelease.version=1.0.0-beta.1

# Release candidate
./gradlew release -Prelease.version=1.0.0-rc.1
```

## Available Gradle Tasks

### Version Management

```bash
./gradlew currentVersion       # Show current version
./gradlew verifyRelease         # Verify release configuration
./gradlew release               # Create release tag
```

### Publishing

```bash
./gradlew :junit-jupiter-db-tester:publishToMavenLocal                       # Test locally
./gradlew :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository # Stage artifacts
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
git tag -l

# Fetch all tags if out of sync
git fetch --tags

# Check current version
./gradlew currentVersion
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
- Check staging repository: `ls -la junit-jupiter-db-tester/build/staging-deploy/`
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

# 2. Check current version
./gradlew currentVersion
# Output: Project version: 1.0.0-SNAPSHOT

# 3. Create release tag (automatically pushes to remote)
./gradlew release

# 4. Build and publish to staging
./gradlew :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          --no-configuration-cache

# 5. Create GitHub Release and deploy to Maven Central
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --no-configuration-cache

# 6. Verify on GitHub and Maven Central
# GitHub: https://github.com/seijikohara/junit-jupiter-db-tester/releases
# Maven Central: https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester

# 7. Next commits will automatically be 1.0.1-SNAPSHOT
```

## References

- [axion-release-plugin Documentation](https://axion-release-plugin.readthedocs.io/)
- [JReleaser Documentation](https://jreleaser.org/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Semantic Versioning](https://semver.org/)
