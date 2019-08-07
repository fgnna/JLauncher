package someday.com.jlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity
{
    private final String TAG = "FullscreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);


        for(ApplicationInfo app : queryFilterAppInfo())
        {

            Log.d(TAG, String.format(" appname: %s    ", app.loadLabel(getPackageManager())));
//            Log.d(TAG, String.format(" appname: %s  flag %s ", app.loadLabel(getPackageManager()),app.flags));
//            Log.d(TAG, String.format(" app logo: %s    ", getLogo(app).toString()));
            Log.d(TAG, String.format(" app xml: %s    ", getPackageManager().getLaunchIntentForPackage(app.packageName)));


        }


    }

    /**
     * 获取图标 bitmap
     */
    public  Drawable getLogo(ApplicationInfo applicationInfo )
    {
        return getPackageManager().getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
    }


    private List<ApplicationInfo> queryFilterAppInfo() {
        PackageManager pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<ApplicationInfo> applicationInfos = new ArrayList<>();

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        Set<String> allowPackages = new HashSet();
        for (ResolveInfo resolveInfo : resolveinfoList) {
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }

        for (ApplicationInfo app : appInfos) {
//            if((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)//通过flag排除系统应用，会将电话、短信也排除掉
//            {
//                applicationInfos.add(app);
//            }
//            if(app.uid > 10000){//通过uid排除系统应用，在一些手机上效果不好
//                applicationInfos.add(app);
//            }
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(app);
            }
        }
        return applicationInfos;
    }

}
