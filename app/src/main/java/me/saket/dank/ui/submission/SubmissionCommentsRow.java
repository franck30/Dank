package me.saket.dank.ui.submission;

/**
 * Represents one row in the comments section.
 */
public interface SubmissionCommentsRow {

  enum Type {
    USER_COMMENT,
    LOAD_MORE_COMMENTS,
  }

  long id();

  Type type();

}
