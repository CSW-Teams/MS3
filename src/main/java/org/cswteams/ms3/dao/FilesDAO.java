package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Config;
import org.cswteams.ms3.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesDAO  extends JpaRepository<Files, Long> {
}
