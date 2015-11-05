package pds.com.codereader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.common.StringUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.util.Arrays.asList;

public class CodeReaderApp extends AppCompatActivity implements OnClickListener {

    public static final String UNKNOWN_TYPE = "Formato desconocido";
    public static final String UNKNOWN_CONTENT = "Error al obtener el contenido";
    public static final String NOT_FOUND_IMAGE_URL = "http://lalala.com";
    private Button scanBtn;
    private TextView formatTxt;
    private TextView contentTxt;
    private ImageView resultImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_reader_app);

        resultImg = (ImageView) findViewById(R.id.result_image);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_code_reader_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan(asList("QR_CODE"));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //We have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            //Just showing the text for that code for now. Probably will be a url and go to that.
            String codeTypeFormatMsg = scanFormat != null ? "TIPO: " + scanFormat : UNKNOWN_TYPE;
            String codeContentMsg = scanFormat != null ? "CONTENIDO: " + scanContent : UNKNOWN_CONTENT;

            formatTxt.setText(codeTypeFormatMsg);
            contentTxt.setText(codeContentMsg);
            //Show image
            resultImg.setImageResource(getImageResource(codeContentMsg));

//            // ImageLoader class instance
//            ImageLoader imgLoader = new ImageLoader(getApplicationContext());
//            imgLoader.displayImage( getImageUrl(scanContent), R.drawable.loader, resultImg);

        }else{
            Toast toast = makeText(getApplicationContext(), "No se pudo escanear!", LENGTH_SHORT);
            toast.show();
        }

    }

    private int getImageResource(String codeContentMsg) {
        String[] msgSections = codeContentMsg.split("\\|");
        if(msgSections != null && msgSections.length > 0 ){
            if(msgSections[0].contains("i-have-no-idea")){
                return R.drawable.i_have_no_idea;
            }
            if(msgSections[0].contains("please-tell-me-more")){
                return R.drawable.please_tell_me_more;
            }
        }
        return R.drawable.not_available;
    }

    private String getImageUrl(String url) {
        if(url.startsWith("http://") || url.startsWith("https://")){
            return url;
        }
        return NOT_FOUND_IMAGE_URL;
    }

}
