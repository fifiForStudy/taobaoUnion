package com.example.taobao.model.domain;

import java.util.List;

public class SelectedPageCategory {

    private boolean success;
    private int code;
    private String message;
    private List<DataDTO> data;

    @Override
    public String toString() {
        return "SelectedPageCategory{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

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

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public static class DataDTO {
        private int type;
        private int favorites_id;
        private String favorites_title;

        @Override
        public String toString() {
            return "DataDTO{" +
                    "type=" + type +
                    ", favorites_id=" + favorites_id +
                    ", favorites_title='" + favorites_title + '\'' +
                    '}';
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getFavorites_id() {
            return favorites_id;
        }

        public void setFavorites_id(int favorites_id) {
            this.favorites_id = favorites_id;
        }

        public String getFavorites_title() {
            return favorites_title;
        }

        public void setFavorites_title(String favorites_title) {
            this.favorites_title = favorites_title;
        }
    }
}
