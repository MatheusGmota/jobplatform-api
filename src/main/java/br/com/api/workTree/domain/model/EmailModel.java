package br.com.api.workTree.domain.model;

import br.com.api.workTree.domain.enums.StatusEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class EmailModel implements Serializable {
    private String emailTo;
    private String subject;
    private String text;
    private StatusEmail statusEmail;
}
