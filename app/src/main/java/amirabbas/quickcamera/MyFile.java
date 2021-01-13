package amirabbas.quickcamera;

public class MyFile {
    public enum State {beforeSend, successful, sending, failed}

    public enum Type {PDF, IMAGE}

    private String path;
    private State state = State.beforeSend;
    private String name;
    private Type type;
    private String message;

    public MyFile(String path) {
        this.path = path;
        name = path.substring(path.lastIndexOf('/') + 1);
        type = name.substring(name.lastIndexOf('.') + 1).toLowerCase().equals("pdf") ? Type.PDF : Type.IMAGE;
    }

    public String getMessage() {

        if (state == State.sending)
            return "این فایل در حال ارسال می‌باشد و نتیجه ارسال هنوز مشخص نشده است.";
        else if (state == State.successful)
            return "این فایل با موفقیت ارسال شده است و اکنون در داخل کامپیوتر شما قابل مشاهده می‌باشد.";
        else if (state == State.beforeSend)
            return "این فایل آماده ارسال می‌باشد";
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public MyFile.State getState() {
        return state;
    }

    public void setState(MyFile.State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
