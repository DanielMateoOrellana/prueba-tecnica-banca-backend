package com.prueba.banca.repository;

import com.prueba.banca.domain.Cuenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, String> {

    List<Cuenta> findByCliente_ClienteId(String clienteId);

    List<Cuenta> findByCliente_Id(Long clienteIdPk);
}
