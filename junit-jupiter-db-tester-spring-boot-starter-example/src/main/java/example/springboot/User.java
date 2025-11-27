package example.springboot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * User entity for database testing demonstration.
 *
 * <p>This is a simple JPA entity representing a user record in the USERS table.
 */
@Entity
@Table(name = "users")
public class User {

  /** The user identifier. */
  @Id private Long id;

  /** The user name. */
  @Column(nullable = false)
  private String name;

  /** The user email address. */
  @Column(nullable = false)
  private String email;

  /**
   * Default constructor required by JPA.
   *
   * <p>JPA requires a no-arg constructor for entity instantiation. Fields are initialized by JPA
   * after construction via reflection.
   */
  @SuppressWarnings("NullAway.Init")
  protected User() {}

  /**
   * Creates a new user with the specified attributes.
   *
   * @param id the user identifier
   * @param name the user name
   * @param email the user email address
   */
  public User(final Long id, final String name, final String email) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.email = Objects.requireNonNull(email, "email must not be null");
  }

  /**
   * Returns the user identifier.
   *
   * @return the user identifier
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the user name.
   *
   * @return the user name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the user email address.
   *
   * @return the user email address
   */
  public String getEmail() {
    return email;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof User other)) {
      return false;
    }
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "User[id=" + id + ", name=" + name + ", email=" + email + "]";
  }
}
