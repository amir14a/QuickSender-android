package amirabbas.quickcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends RecyclerView.Adapter<Adapter.vh> {
    public static final int messageTypeLoading = 1;
    public static final int messageTypeNotLoading = 2;
    public static final int messageTypeFailed = 3;
    private int messageType = 1;
    private String message = "";
    private ArrayList<MyFile> files;
    private Activity context;
    Thread t;

    public Adapter(Activity context) {
        this.context = context;
        files = new ArrayList<>();
    }

    public void addFiles(ArrayList<MyFile> files) {
        this.files.addAll(0, files);
        notifyDataSetChanged();
    }

    public void send() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (FF.getSetting(context, Consts.sKaheshHajm))
                    kaheshHajm();
                if (FF.getSetting(context, Consts.sPDF))
                    createPdf();
                for (MyFile image : files) {
                    if (image != null && MyFile.State.beforeSend == image.getState()) {
                        image.setState(MyFile.State.sending);
                        context.runOnUiThread(Adapter.this::notifyDataSetChanged);
                        File file = new File(image.getPath());
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        Headers.Builder headers = new Headers.Builder();
                        headers.addUnsafeNonAscii("Content-Disposition", "form-data; name=\"aFile\"; filename=\"" + file.getName() + "\"");
                        MultipartBody.Part body = MultipartBody.Part.create(headers.build(), requestFile);
                        RequestBody description = RequestBody.create(MultipartBody.FORM, "This is a description!");
                        Call<ResponseBody> call = ApiClient.getInstance().Upload(description, body);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                image.setState(MyFile.State.successful);
                                context.runOnUiThread(Adapter.this::notifyDataSetChanged);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                image.setState(MyFile.State.failed);
                                image.setMessage("ارسال این فایل با خطا مواجه شده است :(: " + "\n" + t.getMessage());
                                context.runOnUiThread(Adapter.this::notifyDataSetChanged);
                            }
                        });
                    }
                }
            }
        }.start();
    }

    private void createPdf() {
        setMessage("در حال ساختن فایل PDF...", messageTypeLoading);
        try {
            PDDocument document = new PDDocument();
            for (MyFile image : files) {
                if (image != null && image.getType() == MyFile.Type.IMAGE && image.getState() == MyFile.State.beforeSend) {
                    Bitmap b = BitmapFactory.decodeFile(image.getPath());
                    PDPage page = new PDPage(new PDRectangle(b.getWidth(), b.getHeight()));
                    document.addPage(page);
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);
                    PDImageXObject ximage = JPEGFactory.createFromImage(document, b, 0.9f, 72);
                    contentStream.drawImage(ximage, 0, 0);
                    contentStream.close();
                }
            }
            if(document.getPages().getCount()!=0) {
                String pdfPath = context.getCacheDir() + "/PDF_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".pdf";
                document.save(pdfPath);
                document.close();
                files.add(files.get(0) == null ? 1 : 0, new MyFile(pdfPath));
                context.runOnUiThread(this::notifyDataSetChanged);
            }
            deleteMessage();
        } catch (Exception e) {
            setMessage("خطا در ساختن فایل PDF :(" + "\n" + e.getMessage(), messageTypeFailed);
        }
    }

    private void kaheshHajm() {
        setMessage("در حال کاهش حجم تصاویر...", messageTypeLoading);
        try {
            for (MyFile image : files) {
                if (image != null && image.getType() == MyFile.Type.IMAGE && image.getState() == MyFile.State.beforeSend) {
                    Bitmap b = BitmapFactory.decodeFile(image.getPath());
                    b = FF.resizeBitmap(b, 1280);
                    String root = context.getCacheDir().toString();
                    File myDir = new File(root);
                    myDir.mkdirs();
                    File file = new File(myDir, image.getName());
                    if (file.exists())
                        file.delete();
                    FileOutputStream out = new FileOutputStream(file);
                    b.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    image.setPath(file.getPath());
                }
            }
            deleteMessage();
        } catch (Exception e) {
            setMessage("خطا در کاهش حجم تصاویر :(" + "\n" + e.getMessage(), messageTypeFailed);
        }
    }

    public void setMessage(String message, int messageType) {
        if (files.isEmpty() || files.get(0) != null)
            files.add(0, null);
        this.message = message;
        this.messageType = messageType;
        context.runOnUiThread(this::notifyDataSetChanged);
    }

    public void deleteMessage() {
        if (!files.isEmpty() && files.get(0) == null) {
            files.remove(0);
            context.runOnUiThread(this::notifyDataSetChanged);
        }
    }

    public boolean checkInProgress() {
        for (MyFile file : files) {
            if (file != null && file.getState() == MyFile.State.sending)
                return true;
        }
        return false;
    }

    @NonNull
    @Override
    public vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new vh(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull vh holder, int i) {
        if (files.get(i) != null) {
            holder.close.setVisibility(View.INVISIBLE);
            holder.sucsees.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);
            holder.rightLeayout.setVisibility(View.VISIBLE);
            MyFile file = files.get(i);
            holder.name.setText(file.getName());
            if (file.getType() == MyFile.Type.PDF)
                holder.icon.setImageResource(R.drawable.pdf_icon);
            else
                Picasso.get().load("file:///" + file.getPath())
                        .resize(128, 128)
                        .into(holder.icon);
//                setImage(holder.icon, file.getPath());
            if (file.getState() == MyFile.State.sending) {
                holder.sucsees.setVisibility(View.INVISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.sucsees.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.INVISIBLE);
                if (file.getState() == MyFile.State.beforeSend) {
                    holder.sucsees.setVisibility(View.INVISIBLE);
                } else if (file.getState() == MyFile.State.successful) {
                    holder.sucsees.setImageResource(R.drawable.ic_check);
                } else if (file.getState() == MyFile.State.failed) {
                    holder.sucsees.setImageResource(R.drawable.ic_close);
                }
            }
            if (file.getMessage() != null && !file.getMessage().isEmpty())
                holder.rightLeayout.setOnClickListener(view ->
                        new AlertDialog.Builder(context).setMessage(file.getMessage()).show());
        } else //messageViewType
        {
            holder.sucsees.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
            holder.name.setText(message);
            int dp = (int) FF.pxFromDp(context, 15f);
            holder.name.setPadding(dp, dp, dp, dp);
            holder.close.setVisibility(View.VISIBLE);
            holder.close.setOnClickListener(view -> deleteMessage());
            if (messageType == messageTypeLoading) {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.rightLeayout.setVisibility(View.VISIBLE);
            } else
                holder.rightLeayout.setVisibility(View.GONE);
            if (messageType == messageTypeFailed)
                holder.layout.setBackgroundColor(Color.parseColor("#F48FB1"));
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    private void setImage(ImageView imageView, String path) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (t != null) {
                    while (t.isAlive()) {
                    }
                }
                t = this;
                Bitmap b = FF.resizeBitmap(BitmapFactory.decodeFile(path), 200);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(b);
                        FF.playFadeInAnimation(imageView);
                    }
                });
            }
        }.start();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class vh extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView icon, sucsees, close;
        TextView name;
        ProgressBar progressBar;
        RelativeLayout rightLeayout;

        public vh(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linear);
            icon = itemView.findViewById(R.id.icon);
            sucsees = itemView.findViewById(R.id.success);
            name = itemView.findViewById(R.id.txt);
            progressBar = itemView.findViewById(R.id.progress_bar);
            close = itemView.findViewById(R.id.close_btn);
            rightLeayout = itemView.findViewById(R.id.rightLayout);
        }
    }
}
