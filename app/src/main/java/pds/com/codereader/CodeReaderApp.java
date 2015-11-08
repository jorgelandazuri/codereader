package pds.com.codereader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static java.util.Arrays.asList;

public class CodeReaderApp extends AppCompatActivity implements OnClickListener {

    public static final String UNKNOWN_TYPE = "Formato desconocido";
    public static final String UNKNOWN_CONTENT = "Error al obtener el contenido";
    public static final String UNKNOWN_TIME = "Error al calcular el tiempo";
    public static final String SCANNING_ERROR_MSG = "No se pudo escanear!";
    private Button scanBtn;
    private TextView formatTxt;
    private TextView timeTxt;
    private TextView contentTxt;
    private ImageView resultImg;

    private Date startScanTime;
    private Date endScanTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_reader_app);

        resultImg = (ImageView) findViewById(R.id.result_image);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        timeTxt = (TextView)findViewById(R.id.scan_time);
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
            startScanTime = new Date();
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        endScanTime = new Date();
        long timeToScan = -1;
        if(startScanTime!=null){
            timeToScan = endScanTime.getTime() - startScanTime.getTime();
        }

        if (scanningResult != null) {
            formatTxt.setText(getCodeTypeMsg(scanningResult.getFormatName()));
            contentTxt.setText(getContentMsg(scanningResult.getContents()));
            timeTxt.setText(getTimeMsg(timeToScan));
            setImageResource(scanningResult.getContents(), getApplicationContext());

        }else{
            Toast toast = makeText(getApplicationContext(), SCANNING_ERROR_MSG, LENGTH_SHORT);
            toast.show();
        }

    }

    private String getTimeMsg(long timeToScan) {
        return timeToScan >= 0  ? " TIEMPO DE ESCANEO: "+ String.valueOf(timeToScan) + " ms. ": UNKNOWN_TIME;
    }

    @NonNull
    private String getContentMsg(String scanContent) {
        return scanContent != null ? "CONTENIDO\n" + scanContent : UNKNOWN_CONTENT;
    }

    @NonNull
    private String getCodeTypeMsg(String scanFormat) {
        return scanFormat != null ? " TIPO: " + scanFormat + ". " : UNKNOWN_TYPE;
    }

    private void setImageResource(String codeContentMsg, Context context) {
        String[] msgSections = codeContentMsg.split("\\|");
        try{
            String trimmedResourceName = new String(msgSections[0]).trim().replaceAll(" ", "");
            int id = context.getResources().getIdentifier(trimmedResourceName, "drawable", context.getPackageName());
            if(id != 0){
                resultImg.setImageResource(id);
            }else{
                resultImg.setImageResource(R.drawable.not_available);
            }
        } catch (Exception e) {
            Toast toast = makeText(getApplicationContext(), e.toString(), LENGTH_SHORT);
            toast.show();
        }

    }
}
