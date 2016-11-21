package com.zoophy.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.zoophy.genbank.Gene;

/**
 * Maps SQL row to Virus Gene
 * @author devdemetri
 */
public class GeneRowMapper implements RowMapper<Gene> {

	@Override
	public Gene mapRow(ResultSet rs, int rowNum) throws SQLException {
		Gene gene = new Gene();
		gene.setId(rs.getLong("Gene_ID"));
		gene.setAccession(rs.getString("Accession"));
		gene.setName(rs.getString("Normalized_Gene_Name"));
		return gene;
	}

}
