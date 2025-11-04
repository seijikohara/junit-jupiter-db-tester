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
- ✅ Create GitHub Release with changelog
- ✅ Upload signed artifacts to Maven Central
- ✅ Automatically publish to Maven Central

**Step 4: Verify Release**

1. GitHub: `https://github.com/seijikohara/junit-jupiter-db-tester/releases`
2. Maven Central: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester`

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
./gradlew :junit-jupiter-db-tester:jreleaserRelease       # GitHub Release only
./gradlew :junit-jupiter-db-tester:jreleaserDeploy        # Maven Central only
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease   # Both (recommended)
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

### GitHub Release Failed

- Verify `JRELEASER_GITHUB_TOKEN` is set
- Check token has `repo` permissions
- Verify repository name and owner in build configuration

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

# 3. Create release tag
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
