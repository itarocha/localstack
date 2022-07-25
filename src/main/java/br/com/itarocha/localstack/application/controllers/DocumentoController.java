package br.com.itarocha.localstack.application.controllers;

import br.com.itarocha.localstack.domain.entities.ArquivoArmazenado;
import br.com.itarocha.localstack.domain.usecases.ProcessadorArquivoUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
public class DocumentoController {

    private final ProcessadorArquivoUseCase processadorArquivo;

    public DocumentoController(ProcessadorArquivoUseCase fileService){
        this.processadorArquivo = fileService;
    }

    @GetMapping("/test")
    public ResponseEntity teste(){
        processadorArquivo.testar();
        return new ResponseEntity("Sucesso", HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file){
        String key = processadorArquivo.uploadArquivo(file);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ArquivoArmazenado>> listBucket(){
        List<ArquivoArmazenado> lista = processadorArquivo.listarArquivos();
        return ResponseEntity.ok(lista);
    }
}
