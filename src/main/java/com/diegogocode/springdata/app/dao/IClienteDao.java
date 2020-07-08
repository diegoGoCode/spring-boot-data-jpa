package com.diegogocode.springdata.app.dao;

import com.diegogocode.springdata.app.entity.Cliente;
import org.springframework.data.repository.CrudRepository;


public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
