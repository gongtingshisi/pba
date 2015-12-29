package kg.gtss.personalbooksassitant;

import kg.gtss.utils.Log;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class FullDisplayPictureActivity extends Activity {
	Uri mImgUri;
	String mIsbn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle b = this.getIntent().getExtras();
		mImgUri = this.getIntent().getData();
		Log.v(this, "******" + mImgUri);
		// mImgUri = Uri.parse(b.getString("imguri"));
		// mIsbn = b.getString("isbn");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.full_display_pciture);

		ImageView img = (ImageView) findViewById(R.id.img);
		if (null != mImgUri) {
			// here we use large bookcover
			try {
				img.setImageURI(Uri.parse(mImgUri.toString() + ".l"));
			} catch (Exception e) {
				img.setImageURI(Uri.parse(mImgUri.toString()));
			}
			this.findViewById(R.id.prg).setVisibility(View.GONE);
		} else {

		}
	}
}
