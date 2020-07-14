package com.diegogocode.springdata.app.dao;

import com.diegogocode.springdata.app.entity.Cliente;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long> {


}
