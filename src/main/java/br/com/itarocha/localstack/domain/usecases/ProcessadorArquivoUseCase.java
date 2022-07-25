package br.com.itarocha.localstack.domain.usecases;

import br.com.itarocha.localstack.domain.entities.ArquivoArmazenado;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProcessadorArquivoUseCase {
    void testar();
    List<ArquivoArmazenado> listarArquivos();
    String uploadArquivo(MultipartFile arquivo);
}
