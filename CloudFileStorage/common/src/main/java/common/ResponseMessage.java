package common;

public class ResponseMessage extends AbstractMessage {
    private Response response;

    public ResponseMessage(Response response){
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}
