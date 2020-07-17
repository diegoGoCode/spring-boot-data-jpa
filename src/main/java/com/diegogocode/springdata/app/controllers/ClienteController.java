package com.diegogocode.springdata.app.controllers;

import com.diegogocode.springdata.app.entity.Cliente;
import com.diegogocode.springdata.app.services.IClienteService;
import com.diegogocode.springdata.app.services.IUploadFileService;
import com.diegogocode.springdata.app.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

@Controller
@SessionAttributes("cliente")
public class ClienteController {



    private static final String listarClientes = "listar";
    private static final String formularioCliente = "form";
    private static final String verImagen = "ver";

    @Autowired
    private IClienteService iClienteService;

    @Autowired
    private IUploadFileService iUploadFileService;


    @GetMapping(value = "/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename){
        Resource recurso = null;
        try {
            recurso = iUploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @GetMapping(value = "/ver/{id}")
    public String verDetalleCliente(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash){
        Cliente cliente = iClienteService.findOne(id);
        if(cliente==null){
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
            return "redirect:/clientes";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Detalle - ".concat(cliente.getNombre()));
        return ClienteController.verImagen;
    }

    @GetMapping(value = "/clientes")
    public String listarClientes(@RequestParam(name = "page", defaultValue = "0") int page, Model model){
        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Cliente> clientes = iClienteService.findAll(pageRequest);
        PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);

        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("clientes", clientes);
        model.addAttribute("page", pageRender);
        return ClienteController.listarClientes;
    }

    @GetMapping(value = "/form")
    public String mostrarFormularioCliente(Map<String, Object> model){
        Cliente cliente = new Cliente();
        model.put("cliente", cliente);
        model.put("titulo", "Formulario nuevo cliente");
        return ClienteController.formularioCliente;
    }

    @PostMapping(value = "/form")
    public String guardarNuevoCliente(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flas, SessionStatus status){
        if(result.hasErrors()){
            model.addAttribute("titulo", "Formulario nuevo cliente");
            return "form";
        }
        if(!foto.isEmpty()){
            if(cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0){
                iUploadFileService.delete(cliente.getFoto());
            }
            String uniqueFilename = null;
            try {
                uniqueFilename = iUploadFileService.copy(foto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            flas.addFlashAttribute("info", "Se ha subido correctamente '"+uniqueFilename+"'");
            cliente.setFoto(uniqueFilename);
        }
        String mensajeFlash = (cliente.getId() != null) ? "Cliente editado con exito!" : "Cliente creado con exito!";
        iClienteService.save(cliente);
        status.setComplete();
        flas.addFlashAttribute("success", mensajeFlash);
        return "redirect:clientes";
    }

    @GetMapping("/form/{id}")
    public String actualizarCliente(@PathVariable Long id, Map<String, Object> model, RedirectAttributes flash){
        Cliente cliente = null;
        if(id > 0){
            cliente = iClienteService.findOne(id);
            if(cliente == null){
                flash.addFlashAttribute("error", "El cliente con el ID: ".concat(id.toString()).concat(" no existe en la base de datos"));
                return "redirect:/clientes";
            }
        }else {
            flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
            return "redirect:/clientes";
        }
        model.put("cliente", cliente);
        model.put("titulo", "Editar Cliente");
        return ClienteController.formularioCliente;
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes flash){
        if(id > 0){
            Cliente cliente = iClienteService.findOne(id);

            iClienteService.delete(id);
            flash.addFlashAttribute("success", "Cliente eliminado con exito!");
            if(iUploadFileService.delete(cliente.getFoto())){
                flash.addFlashAttribute("info", "Foto "+ cliente.getFoto()+ " eliminada con exito!");
            }
        }
        return "redirect:/clientes";
    }

}
