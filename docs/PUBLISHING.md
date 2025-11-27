# Publishing to Maven Central

This document describes how to publish JUnit Jupiter DB Tester modules to Maven Central using the Central Portal and JReleaser.

## Published Modules

The following modules are published to Maven Central:

| Module | Artifact ID | Description |
|--------|-------------|-------------|
| [junit-jupiter-db-tester](../junit-jupiter-db-tester/) | `junit-jupiter-db-tester` | Core library |
| [junit-jupiter-db-tester-bom](../junit-jupiter-db-tester-bom/) | `junit-jupiter-db-tester-bom` | Bill of Materials |
| [junit-jupiter-db-tester-spring-boot-starter](../junit-jupiter-db-tester-spring-boot-starter/) | `junit-jupiter-db-tester-spring-boot-starter` | Spring Boot Starter |

All modules share the same version and are released together.

## Prerequisites

### 1. Central Portal Account & Namespace

**Create Account**:
- Visit [central.sonatype.com](https://central.sonatype.com/)
- Sign up with your email address

**Verify Namespace** (`io.github.seijikohara`):

1. In Central Portal, navigate to "Namespaces"
2. Click "Add Namespace"
3. Enter `io.github.seijikohara`
4. For GitHub verification:
   - Create a temporary public repository: `https://github.com/seijikohara/OSSRH-<random-id>` (use the ID provided by Central Portal)
   - Click "Verify" in Central Portal
   - Delete the temporary repository after verification

**Alternative**: For custom domains, add a TXT DNS record instead.

### 2. Generate API Token

1. Log in to [central.sonatype.com/account](https://central.sonatype.com/account)
2. Click **"Generate User Token"**
3. Save the username and password (token) securely

### 3. GPG Key Setup

Generate and configure GPG key for artifact signing:

```bash
# Generate new GPG key (RSA 4096-bit recommended)
gpg --full-generate-key

# Select:
# - (1) RSA and RSA
# - 4096 bits
# - 0 = key does not expire (or set expiration as needed)
# - Enter your name and email (must match GitHub account)

# List keys to get Key ID
gpg --list-secret-keys --keyid-format=short

# Example output:
# sec   rsa4096/ABCD1234 2024-01-01 [SC]

# Publish public key to key servers
gpg --keyserver keyserver.ubuntu.com --send-keys ABCD1234
gpg --keyserver keys.openpgp.org --send-keys ABCD1234

# Test signing
echo "test" | gpg --clearsign
```

### 4. Configure Credentials

**Required**: Create or update `~/.gradle/gradle.properties`:

```properties
# GPG passphrase for Gradle signing plugin (required for non-interactive signing)
signing.gnupg.passphrase=your-gpg-passphrase-from-step-3
```

**Required**: Create or update `~/.jreleaser/config.properties`:

```properties
# Maven Central Portal credentials
JRELEASER_MAVENCENTRAL_SONATYPE_USERNAME=your-username-from-step-2
JRELEASER_MAVENCENTRAL_SONATYPE_PASSWORD=your-password-token-from-step-2

# GitHub Personal Access Token (for gh CLI, optional for manual release creation)
# Create token at: https://github.com/settings/tokens
# Scopes needed: repo, workflow
JRELEASER_GITHUB_TOKEN=your-github-personal-access-token
```

**Alternative**: Set as environment variables:

```bash
export JRELEASER_MAVENCENTRAL_SONATYPE_USERNAME="your-username"
export JRELEASER_MAVENCENTRAL_SONATYPE_PASSWORD="your-token"
```

## Publishing Process

There are two ways to publish releases:

1. **GitHub Actions (Recommended)**: Automated workflow with validation and approval
2. **Local Release**: Manual release from your development machine

---

## Option 1: GitHub Actions Release (Recommended)

The recommended way to release is using the GitHub Actions workflow, which provides:
- Version validation (MAJOR.MINOR.PATCH format)
- Automatic check that version is newer than existing tags
- Dry-run mode for testing
- Manual approval gate before actual deployment

### Prerequisites (One-time Setup)

1. **GitHub Environment**: Create `maven-central` environment in repository settings
   - Settings → Environments → New environment
   - Name: `maven-central`
   - Add required reviewers (yourself)

2. **GitHub Secrets**: Add the following secrets in repository settings
   - Settings → Secrets and variables → Actions → New repository secret

   | Secret | Description |
   |--------|-------------|
   | `GPG_PRIVATE_KEY` | GPG private key (ASCII armor format: `gpg --armor --export-secret-keys KEY_ID`) |
   | `GPG_PASSPHRASE` | GPG key passphrase |
   | `MAVEN_CENTRAL_USERNAME` | Same as `JRELEASER_MAVENCENTRAL_USERNAME` in `~/.jreleaser/config.properties` |
   | `MAVEN_CENTRAL_TOKEN` | Same as `JRELEASER_MAVENCENTRAL_TOKEN` in `~/.jreleaser/config.properties` |

### Release Steps

**Step 1: Run Dry-Run (Optional but Recommended)**

1. Go to Actions → Release → Run workflow
2. Enter version (e.g., `1.2.0`)
3. Check "Dry-run mode"
4. Click "Run workflow"

This validates the version, builds, tests, and runs JReleaser dry-run without creating tags or publishing.

**Step 2: Run Actual Release**

1. Go to Actions → Release → Run workflow
2. Enter version (e.g., `1.2.0`)
3. Leave "Dry-run mode" unchecked
4. Click "Run workflow"
5. After `build-and-dry-run` job completes, approve the deployment in the `maven-central` environment
6. Wait for the `release` job to complete

**Operations Performed:**
- Version validation
- Build and test all modules
- Deploy to Maven Central
- Create Git tag (e.g., `v1.2.0`)
- Create GitHub Release with auto-generated notes

### Verify Release

- **GitHub Release**: https://github.com/seijikohara/junit-jupiter-db-tester/releases
- **Maven Central**: https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester

---

## Option 2: Local Release

For local releases from your development machine, follow the steps below.

### Step 1: Ensure Clean State

```bash
# Check current version
./gradlew currentVersion
# Output: Project version: 1.1.1-SNAPSHOT

# Ensure all changes are committed
git status

# Run all tests
./gradlew clean build test
```

### Step 2: Create Release Tag

```bash
# Create release tag (e.g., v1.2.0)
./gradlew release

# Or specify version explicitly
./gradlew release -Prelease.version=1.2.0
```

This creates a Git tag (e.g., `v1.2.0`) and automatically pushes it to the remote repository.

### Step 3: Build and Stage All Modules

Build and stage all publishable modules (core library, BOM, and Spring Boot Starter):

```bash
# Clean build with signatures for all modules
./gradlew :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester-bom:clean \
          :junit-jupiter-db-tester-bom:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester-spring-boot-starter:clean \
          :junit-jupiter-db-tester-spring-boot-starter:build \
          :junit-jupiter-db-tester-spring-boot-starter:publishAllPublicationsToStagingRepository \
          --no-configuration-cache
```

This generates for each module:
- Main JAR (or POM for BOM)
- Sources JAR (except BOM)
- Javadoc JAR (except BOM)
- POM file
- GPG signatures (`.asc` files)
- Checksums (MD5, SHA-1, SHA-256, SHA-512)

Artifacts are staged in each module's `build/staging-deploy/` directory.

### Step 4: Deploy to Maven Central and Create GitHub Release

```bash
# Deploy all modules to Maven Central and create GitHub Release
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --no-configuration-cache
```

**Operations performed:**
1. Creates GitHub Release with auto-generated changelog
2. Uploads artifacts from all staging directories to Central Portal
3. Validates artifacts against Maven Central requirements
4. Automatically publishes to Maven Central (no manual approval required)

**Note**: This can take 30-45 minutes for the deployment validation and publishing.

**Dry run** (testing only):
```bash
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --dry-run --no-configuration-cache
```

### Step 5: Verify Publication

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

### Step 6: Update to Next Development Version

The version automatically becomes the next SNAPSHOT after the tag is created. No manual version updates needed!

```bash
./gradlew currentVersion
# Output: Project version: 1.2.1-SNAPSHOT
```

## Alternative Workflows

### Quick Release (All-in-One)

```bash
./gradlew release \
          :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester-bom:clean \
          :junit-jupiter-db-tester-bom:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester-spring-boot-starter:clean \
          :junit-jupiter-db-tester-spring-boot-starter:build \
          :junit-jupiter-db-tester-spring-boot-starter:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester:jreleaserFullRelease \
          --no-configuration-cache
```

**Warning**: Use this approach only after successfully completing at least one step-by-step release.

## Available Gradle Tasks

### Version Management

```bash
./gradlew currentVersion       # Show current version
./gradlew release               # Create and push release tag
./gradlew release -Prelease.version=2.0.0  # Specific version
./gradlew verifyRelease         # Verify release configuration
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

### GPG Signing Errors

**Error: "Inappropriate ioctl for device"**

This error occurs when GPG cannot prompt for passphrase in non-TTY environments.

**Solution**: Add GPG passphrase to `~/.gradle/gradle.properties`:

```properties
signing.gnupg.passphrase=your-gpg-passphrase
```

**Other GPG issues**:

```bash
# Ensure gpg-agent is running
gpg-agent --daemon

# Test signing (may fail in non-TTY environment)
echo "test" | gpg --clearsign

# List available keys
gpg --list-secret-keys --keyid-format=short

# If key not found, check key location
gpgconf --list-dirs
```

### Credentials Not Found

```bash
# Verify environment variables
echo $JRELEASER_MAVENCENTRAL_SONATYPE_USERNAME
echo $JRELEASER_GPG_PASSPHRASE
echo $JRELEASER_GITHUB_TOKEN

# Or check config file
cat ~/.jreleaser/config.properties
```

### Maven Central Validation Errors

JReleaser automatically validates:
- GPG signatures (all `.asc` files)
- Checksums (MD5, SHA-1, SHA-256, SHA-512)
- Sources JAR present
- Javadoc JAR present
- POM completeness (name, description, URL, license, developers, SCM)

If validation fails, check:
```bash
# Verify staged artifacts for each module
ls -la junit-jupiter-db-tester/build/staging-deploy/io/github/seijikohara/junit-jupiter-db-tester/
ls -la junit-jupiter-db-tester-bom/build/staging-deploy/io/github/seijikohara/junit-jupiter-db-tester-bom/
ls -la junit-jupiter-db-tester-spring-boot-starter/build/staging-deploy/io/github/seijikohara/junit-jupiter-db-tester-spring-boot-starter/

# Check JReleaser logs
cat junit-jupiter-db-tester/build/jreleaser/trace.log
```

### Maven Central Deployment Timeout

**Warning**: "Deployment timeout exceeded. However, the remote operation may still be successful."

This is normal for initial deployments, which can take 30-45 minutes. The deployment continues in the background.

**Verification**:
1. Log in to [Central Portal](https://central.sonatype.com/publishing)
2. Check deployment status (look for "PUBLISHED" status)
3. Wait for Maven Central replication (additional 10-30 minutes)

### GitHub Release Creation Failed

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

### Configuration Cache Issues

Always use `--no-configuration-cache` with JReleaser tasks due to compatibility:

```bash
# Correct
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --no-configuration-cache

# Incorrect (will fail)
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease
```

## Best Practices

1. **Test Locally First**: Always use `publishToMavenLocal` before releasing
2. **Use Dry Run**: Test with `--dry-run` for first-time releases
3. **Verify Namespace**: Ensure namespace is verified in Central Portal before releasing
4. **Backup GPG Keys**: Store GPG keys securely (export and backup)
5. **Document Credentials**: Keep credential locations documented for team members
6. **Monitor Releases**: Watch GitHub Releases and Maven Central for publication status
7. **Follow SemVer**: Use semantic versioning (major.minor.patch)

## References

- [Central Portal Documentation](https://central.sonatype.org/)
- [JReleaser Documentation](https://jreleaser.org/guide/latest/)
- [JReleaser Maven Central Guide](https://jreleaser.org/guide/latest/reference/deploy/maven/maven-central.html)
- [axion-release-plugin Documentation](https://axion-release-plugin.readthedocs.io/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
