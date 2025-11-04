# Publishing to Maven Central

This document describes how to publish `junit-jupiter-db-tester` to Maven Central using the Central Portal and JReleaser.

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

Create or update `~/.jreleaser/config.properties`:

```properties
# Maven Central Portal credentials
JRELEASER_MAVENCENTRAL_SONATYPE_USERNAME=your-username-from-step-2
JRELEASER_MAVENCENTRAL_SONATYPE_PASSWORD=your-password-token-from-step-2

# GPG passphrase
JRELEASER_GPG_PASSPHRASE=your-gpg-passphrase-from-step-3

# GitHub Personal Access Token (for creating releases)
# Scopes needed: repo (full control of private repositories)
JRELEASER_GITHUB_TOKEN=your-github-personal-access-token
```

**Alternative**: Set as environment variables:

```bash
export JRELEASER_MAVENCENTRAL_SONATYPE_USERNAME="your-username"
export JRELEASER_MAVENCENTRAL_SONATYPE_PASSWORD="your-token"
export JRELEASER_GPG_PASSPHRASE="your-passphrase"
export JRELEASER_GITHUB_TOKEN="your-github-token"
```

## Publishing Process

### Step 1: Ensure Clean State

```bash
# Check current version
./gradlew currentVersion
# Output: Project version: 0.1.0-SNAPSHOT

# Ensure all changes are committed
git status

# Run all tests
./gradlew clean build test
```

### Step 2: Create Release Tag

```bash
# Create release tag (this will create v1.0.0)
./gradlew release

# Or specify version explicitly
./gradlew release -Prelease.version=1.0.0
```

This creates and pushes a Git tag (e.g., `v1.0.0`).

### Step 3: Build and Stage Artifacts

```bash
# Clean build with signatures
./gradlew :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          --no-configuration-cache
```

This generates:
- Main JAR (`junit-jupiter-db-tester-1.0.0.jar`)
- Sources JAR (`junit-jupiter-db-tester-1.0.0-sources.jar`)
- Javadoc JAR (`junit-jupiter-db-tester-1.0.0-javadoc.jar`)
- POM file
- GPG signatures (`.asc` files)
- Checksums (MD5, SHA-1, SHA-256, SHA-512)

All artifacts are staged in `junit-jupiter-db-tester/build/staging-deploy/`

### Step 4: Deploy to Maven Central

```bash
# Full release: GitHub Release + Maven Central deployment
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --no-configuration-cache
```

**What happens:**
1. ✅ Creates GitHub Release with auto-generated changelog
2. ✅ Uploads artifacts to Central Portal
3. ✅ Validates artifacts (Maven Central rules)
4. ✅ Automatically publishes to Maven Central (no manual approval!)

**Dry run** (testing only):
```bash
./gradlew :junit-jupiter-db-tester:jreleaserFullRelease --dry-run --no-configuration-cache
```

### Step 5: Verify Publication

**GitHub Release** (immediate):
- Visit: `https://github.com/seijikohara/junit-jupiter-db-tester/releases`
- Verify changelog and assets

**Maven Central** (10-30 minutes):
- Check: `https://central.sonatype.com/artifact/io.github.seijikohara/junit-jupiter-db-tester`
- Verify version, POM metadata, and signatures

**Test Download**:
```gradle
dependencies {
    testImplementation("io.github.seijikohara:junit-jupiter-db-tester:1.0.0")
}
```

### Step 6: Update to Next Development Version

The version automatically becomes `1.0.1-SNAPSHOT` after the tag is created. No manual version updates needed!

```bash
./gradlew currentVersion
# Output: Project version: 1.0.1-SNAPSHOT
```

## Alternative Workflows

### Quick Release (All-in-One)

```bash
# One command for the entire release process
./gradlew release \
          :junit-jupiter-db-tester:clean \
          :junit-jupiter-db-tester:build \
          :junit-jupiter-db-tester:publishAllPublicationsToStagingRepository \
          :junit-jupiter-db-tester:jreleaserFullRelease \
          --no-configuration-cache
```

⚠️ **Recommended**: Use this only after successfully completing at least one manual release.

### GitHub Release Only (No Maven Central)

```bash
./gradlew :junit-jupiter-db-tester:jreleaserRelease --no-configuration-cache
```

### Maven Central Only (No GitHub Release)

```bash
./gradlew :junit-jupiter-db-tester:jreleaserDeploy --no-configuration-cache
```

## Available Gradle Tasks

### Version Management

```bash
./gradlew currentVersion       # Show current version
./gradlew release               # Create and push release tag
./gradlew release -Prelease.version=2.0.0  # Specific version
./gradlew verifyRelease         # Verify release configuration
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

### GPG Signing Errors

```bash
# Ensure gpg-agent is running
gpg-agent --daemon

# Test signing
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
- ✅ GPG signatures (all .asc files)
- ✅ Checksums (MD5, SHA-1, SHA-256, SHA-512)
- ✅ Sources JAR present
- ✅ Javadoc JAR present
- ✅ POM completeness (name, description, URL, license, developers, SCM)

If validation fails, check:
```bash
# Verify staged artifacts
ls -la junit-jupiter-db-tester/build/staging-deploy/io/github/seijikohara/junit-jupiter-db-tester/1.0.0/

# Check JReleaser logs
cat junit-jupiter-db-tester/build/jreleaser/trace.log
```

### GitHub Release Creation Failed

- Verify `JRELEASER_GITHUB_TOKEN` is set
- Check token has `repo` permissions
- Verify repository owner and name in configuration
- Check internet connectivity

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
