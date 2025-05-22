-- Inserir categorias
INSERT INTO categoria (id, nome) VALUES (1, 'Informática');
INSERT INTO categoria (id, nome) VALUES (2, 'Livros');

-- Inserir produtos
INSERT INTO produto (id, nome, preco, categoria_id) VALUES (1, 'Notebook', 3500.00, 1);
INSERT INTO produto (id, nome, preco, categoria_id) VALUES (2, 'Mouse', 80.00, 1);
INSERT INTO produto (id, nome, preco, categoria_id) VALUES (3, 'Livro de Java', 120.00, 2); 