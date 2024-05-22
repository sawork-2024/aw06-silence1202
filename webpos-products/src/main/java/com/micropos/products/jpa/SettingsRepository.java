package com.micropos.products.jpa;

import com.micropos.products.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository  extends JpaRepository<Settings, Long> {
}
