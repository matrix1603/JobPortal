package models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {

    private int statusCode;
    private Object data;
    private String errorMessage;

    public ApplicationResponse(int statusCode, Object data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public ApplicationResponse( String errorMessage,int statusCode) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }


}
