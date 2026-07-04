-- Full-text index backing the smart search (MATCH ... AGAINST in OfertaRepository.busquedaInteligente).
-- Runs after Hibernate creates the schema (spring.jpa.defer-datasource-initialization=true).
-- The MATCH() column list in queries must match this column list exactly.
-- NOTE: ADD FULLTEXT is not idempotent (MySQL has no clean IF NOT EXISTS for indexes). This is safe because
-- the app runs with ddl-auto=create-drop, so the table is recreated every boot.
ALTER TABLE oferta ADD FULLTEXT INDEX ft_oferta (titulo, empresa, descripcion, ubicacion);
