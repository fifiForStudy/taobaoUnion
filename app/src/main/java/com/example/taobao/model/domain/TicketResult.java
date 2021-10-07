package com.example.taobao.model.domain;

public class TicketResult {

    private boolean success;
    private int code;
    private String message;
    private DataDTO data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TicketResult{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataDTO {
        @Override
        public String toString() {
            return "DataDTO{" +
                    "tbk_tpwd_create_response=" + tbk_tpwd_create_response +
                    '}';
        }

        private TbkTpwdCreateResponseDTO tbk_tpwd_create_response;

        public TbkTpwdCreateResponseDTO getTbk_tpwd_create_response() {
            return tbk_tpwd_create_response;
        }

        public void setTbk_tpwd_create_response(TbkTpwdCreateResponseDTO tbk_tpwd_create_response) {
            this.tbk_tpwd_create_response = tbk_tpwd_create_response;
        }

        public static class TbkTpwdCreateResponseDTO {
            @Override
            public String toString() {
                return "TbkTpwdCreateResponseDTO{" +
                        "data=" + data +
                        ", request_id='" + request_id + '\'' +
                        '}';
            }

            private DataBean data;
            private String request_id;

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public String getRequest_id() {
                return request_id;
            }

            public void setRequest_id(String request_id) {
                this.request_id = request_id;
            }

            public static class DataBean {
                @Override
                public String toString() {
                    return "DataBean{" +
                            "model='" + model + '\'' +
                            '}';
                }

                private String model;

                public String getModel() {
                    return model;
                }

                public void setModel(String model) {
                    this.model = model;
                }
            }
        }
    }
}
