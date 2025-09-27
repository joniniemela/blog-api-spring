INSERT INTO users (id, username, email, password) VALUES
                                                      (1, 'testuser', 'test@example.com', 'hashedpassword'),
                                                      (2, 'author1', 'author1@example.com', 'hashedpassword');

INSERT INTO blogs (id, title, content, author_id, created_at, updated_at) VALUES
                                                      (1, 'Test Blog 1', 'This is test content 1', 1, NOW(), NOW()),
                                                      (2, 'Test Blog 2', 'This is test content 2', 2, NOW(), NOW());