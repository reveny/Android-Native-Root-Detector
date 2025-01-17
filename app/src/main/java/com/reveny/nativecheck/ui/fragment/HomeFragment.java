package com.reveny.nativecheck.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.textview.MaterialTextView;
import com.reveny.nativecheck.App;
import com.reveny.nativecheck.BuildConfig;
import com.reveny.nativecheck.R;
import com.reveny.nativecheck.databinding.DialogAboutBinding;
import com.reveny.nativecheck.databinding.FragmentHomeBinding;
import com.reveny.nativecheck.ui.activity.SettingsActivity;
import com.reveny.nativecheck.ui.dialog.BlurBehindDialogBuilder;
import com.reveny.nativecheck.ui.util.chrome.LinkTransformationMethod;
import com.reveny.nativecheck.util.SnowView;
import com.reveny.nativecheck.util.ThemeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.material.app.LocaleDelegate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class HomeFragment extends BaseFragment {
    private static boolean askedForUpdate = false;
    private static boolean isLoaded = false;
    private DetectionData[] cachedDetections;

    private static final String UPDATE_JSON_URL = "https://dl.reveny.me/update.json";

    /*
    static {
        System.loadLibrary("reveny");
    }
     */

    public DetectionData[] getDetections(PackageManager pm, boolean enableExperimental) {
        return null;
    }

    private FragmentHomeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!askedForUpdate) {
            askedForUpdate = true;
            checkForUpdates();
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_about).setOnMenuItemClickListener(item -> {
            showAbout();
            return true;
        });

        menu.findItem(R.id.menu_settings).setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(requireActivity(), SettingsActivity.class);
            startActivity(intent);

            return true;
        });

        MenuItem dayNightItem = menu.findItem(R.id.menu_dayNight);
        String currentTheme = App.getPreferences().getString("dark_theme", ThemeUtil.MODE_NIGHT_FOLLOW_SYSTEM);
        boolean isNightMode = ThemeUtil.getDarkTheme(currentTheme) == AppCompatDelegate.MODE_NIGHT_YES;

        dayNightItem.setIcon(isNightMode ? R.drawable.ic_light_mode : R.drawable.ic_outline_dark_mode_24);
        dayNightItem.setOnMenuItemClickListener(item -> {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null) {
                String newTheme = isNightMode ? ThemeUtil.MODE_NIGHT_NO : ThemeUtil.MODE_NIGHT_YES;
                AppCompatDelegate.setDefaultNightMode(ThemeUtil.getDarkTheme(newTheme));
                item.setIcon(isNightMode ? R.drawable.ic_light_mode : R.drawable.ic_outline_dark_mode_24);
                App.getPreferences().edit().putString("dark_theme", newTheme).apply();
                activity.recreate();
            }
            return true;
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Setup toolbar and related UI elements
        setupToolbar(binding.toolbar, binding.clickView, R.string.app_name, R.menu.menu_home);
        binding.toolbar.setNavigationIcon(null);
        binding.appBar.setLiftable(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isSnowDisabled = sharedPreferences.getBoolean("disable_snow", false);
        boolean enableExperimental = sharedPreferences.getBoolean("enable_experimental", true);

        if (isSnowDisabled) {
            binding.snowView.setVisibility(View.GONE);
        } else {
            binding.snowView.setVisibility(View.VISIBLE);
            SnowView snowView = binding.getRoot().findViewById(R.id.snow_view);
            snowView.post(snowView::invalidate);
        }

        // If already loaded, skip loading and display cached data
        if (isLoaded) {
            showLoading(false);
            displayDetections(cachedDetections);
        } else {
            showLoading(true);
            loadDetections(enableExperimental);
        }

        binding.nestedScrollView.getBorderViewDelegate().setBorderVisibilityChangedListener((top, oldTop, bottom, oldBottom) -> binding.appBar.setLifted(!top));

        String deviceInfo = getString(R.string.device_info, getDevice(), Build.DEVICE);
        binding.Device.setText(String.format(getString(R.string.sysinfo_device), deviceInfo));

        String androidVersion = Build.VERSION.RELEASE;
        binding.AndroidVersion.setText(String.format(getString(R.string.sysinfo_android_version), androidVersion));

        String kernelVersion = getKernelVersion();
        binding.KernelVersion.setText(String.format(getString(R.string.sysinfo_kernel_version), kernelVersion));

        PackageManager packageManager = requireContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(requireContext().getPackageName(), 0);

            String versionName = packageInfo.versionName;
            binding.AppVersion.setText(String.format(getString(R.string.appinfo_version), versionName));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("RevenyDetector", "Package name not found.", e);
            binding.AppVersion.setText(String.format(getString(R.string.appinfo_version), getString(R.string.appinfo_unknown_version)));
        }
        binding.Signature.setText(String.format(getString(R.string.appinfo_signature), getSignature()));

        binding.ExperimentalEnabled.setText(String.format(getString(R.string.experimental_detections), enableExperimental ? getString(R.string.true_a) : getString(R.string.false_a)));
        return binding.getRoot();
    }

    private void loadDetections(boolean enableExperimental) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DetectionData[] detections = getDetections(requireActivity().getPackageManager(), enableExperimental);
            cachedDetections = detections;

            mainHandler.post(() -> {
                isLoaded = true;
                showLoading(false);
                displayDetections(detections);
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void displayDetections(DetectionData[] detections) {
        if (binding == null) {
            Log.w("HomeFragment", "Binding is null. Skipping displayDetections operation.");
            return;
        }

        if (detections == null || detections.length == 0) {
            binding.statusTitle.setText(R.string.the_environment_is_normal);
            binding.statusSummary.setText(R.string.description_normal);
            return;
        }

        LinearLayout detectionsLayout = binding.container;

        for (DetectionData detection : detections) {
            View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.detection_card, detectionsLayout, false);
            LinearLayout detectionLayout = cardView.findViewById(R.id.detections_layout);

            MaterialTextView detailsTextView = detectionLayout.findViewById(R.id.detection_title);
            detailsTextView.setText(detection.name);

            MaterialTextView materialTextView = new MaterialTextView(requireContext());
            materialTextView.setText(detection.description);

            cardView.setOnLongClickListener(v -> {
                copyToClipboard("Detection Detail", detection.description);
                return true;
            });

            detectionLayout.addView(materialTextView);
            detectionsLayout.addView(cardView, 1);
        }
    }

    private void copyToClipboard(String label, String text) {
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        }

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    private void showLoading(boolean isLoading) {
        if (binding == null) {
            Log.w("HomeFragment", "Binding is null. Skipping showLoading operation.");
            return;
        }
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.nestedScrollView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        binding.appBar.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private String getDevice() {
        String manufacturer = Character.toUpperCase(Build.MANUFACTURER.charAt(0)) + Build.MANUFACTURER.substring(1);
        if (!Build.BRAND.equals(Build.MANUFACTURER)) {
            manufacturer += " " + Character.toUpperCase(Build.BRAND.charAt(0)) + Build.BRAND.substring(1);
        }
        manufacturer += " " + Build.MODEL + " ";
        return manufacturer;
    }

    public String getKernelVersion() {
        StringBuilder kernelVersion = new StringBuilder();
        try {
            // Execute the uname command to get the kernel version
            Process process = Runtime.getRuntime().exec("uname -r");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                kernelVersion.append(line);
            }
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Split the kernel version string by hyphen and return the first part
        String[] parts = kernelVersion.toString().split("-");
        return parts[0];
    }

    public void setSignature(boolean valid) {
        if (binding == null) {
            Log.w("HomeFragment", "Binding is null. Skipping displayDetections operation.");
            return;
        }

        binding.SignatureValid.setText(String.format(getString(R.string.appinfo_is_signature_valid), (valid ? getString(R.string.true_b) : getString(R.string.false_b))));

        if (!valid)
        {
            binding.SignatureValid.setTextColor(Color.RED);
            binding.Signature.setTextColor(Color.RED);
        }
    }

    public void showToast(String message) {
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    private String getSignature() {
        try {
            PackageManager pm = requireContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(requireContext().getPackageName(), PackageManager.GET_SIGNATURES);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();
                digest.update(signatureBytes);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("RevenyDetector", "Package name not found.", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e("RevenyDetector", "SHA-256 algorithm not found.", e);
        }
        return null;
    }

    public static class AboutDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            DialogAboutBinding binding = DialogAboutBinding.inflate(getLayoutInflater(), null, false);
            setupAboutDialog(binding);
            return new BlurBehindDialogBuilder(requireContext()).setView(binding.getRoot()).create();
        }

        private void setupAboutDialog(DialogAboutBinding binding) {
            binding.designAboutTitle.setText(R.string.app_name);
            binding.designAboutInfo.setMovementMethod(LinkMovementMethod.getInstance());
            binding.designAboutInfo.setTransformationMethod(new LinkTransformationMethod(requireActivity()));
            binding.designAboutInfo.setText(HtmlCompat.fromHtml(getString(
                    R.string.about_view_source_code,
                    "<b><a href=\"https://t.me/reveny1\">Telegram</a></b>",
                    "<b><a href=\"https://github.com/reveny/\">Reveny</a></b>"), HtmlCompat.FROM_HTML_MODE_LEGACY));
            binding.designAboutVersion.setText(String.format(LocaleDelegate.getDefaultLocale(), "%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        }
    }

    private void showAbout() {
        new AboutDialog().show(getChildFragmentManager(), "about");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isLoaded = false;
    }

    private void checkForUpdates() {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_JSON_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Scanner scanner = new Scanner(connection.getInputStream());
                    StringBuilder jsonBuilder = new StringBuilder();
                    while (scanner.hasNext()) {
                        jsonBuilder.append(scanner.nextLine());
                    }
                    scanner.close();
                    connection.disconnect();

                    JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
                    JSONArray updates = jsonObject.getJSONArray("updates");

                    String currentVersion = getCurrentVersion();
                    String latestVersion = currentVersion;
                    String latestApkName = "";
                    String latestApkUrl = "";

                    // Find the highest version
                    for (int i = 0; i < updates.length(); i++) {
                        JSONObject update = updates.getJSONObject(i);
                        String version = update.getString("version");
                        String apkName = update.getString("apkName");
                        String apkUrl = update.getString("apkUrl");

                        if (isVersionNewer(version, latestVersion)) {
                            latestVersion = version;
                            latestApkName = apkName;
                            latestApkUrl = apkUrl;
                        }
                    }

                    if (!latestVersion.equals(currentVersion) && !latestApkUrl.isEmpty()) {
                        String finalLatestVersion = latestVersion;
                        String finalLatestApkUrl = latestApkUrl;
                        String finalLatestApkName = latestApkName;
                        requireActivity().runOnUiThread(() -> showUpdateDialog(finalLatestVersion, finalLatestApkUrl, finalLatestApkName));
                    }
                } else {
                    connection.disconnect();
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), getString(R.string.failed_to_get_update_info), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("RevenyDetector", "Error checking updates", e);
                // requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Error checking updates: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String getCurrentVersion() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
        }
    }

    private boolean isVersionNewer(String version1, String version2) {
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");
        for (int i = 0; i < Math.max(v1.length, v2.length); i++) {
            int num1 = i < v1.length ? Integer.parseInt(v1[i]) : 0;
            int num2 = i < v2.length ? Integer.parseInt(v2[i]) : 0;
            if (num1 > num2) return true;
            if (num1 < num2) return false;
        }
        return false;
    }

    private void showUpdateDialog(String latestVersion, String apkUrl, String apkName) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(getString(R.string.update_available))
                .setMessage(getString(R.string.version_is_available_1) + latestVersion + getString(R.string.version_is_available_2))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.update_confirm), (dialog, which) -> downloadAndInstallApk(apkUrl, apkName))
                .setNegativeButton(getString(R.string.update_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void downloadAndInstallApk(String apkUrl, String apkName) {
        File apkFile = new File(requireActivity().getCacheDir(), apkName);

        if (apkFile.exists()) {
            Log.d("RevenyDetector", "APK already exists in cache: " + apkFile.getAbsolutePath());
            installApk(apkFile);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.update_downloading));
        builder.setMessage(getString(R.string.update_wait));
        builder.setCancelable(false);

        ProgressBar progressBar = new ProgressBar(requireActivity(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(50, 0, 50, 0);
        progressBar.setLayoutParams(layoutParams);

        LinearLayout layout = new LinearLayout(requireActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 0, 20, 0);
        layout.addView(progressBar);

        builder.setView(layout);

        AlertDialog progressDialog = builder.create();
        progressDialog.show();

        new Thread(() -> {
            try {
                URL url = new URL(apkUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    int fileLength = connection.getContentLength();

                    try (InputStream inputStream = connection.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(apkFile)) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        long totalBytesRead = 0;

                        Handler handler = new Handler(Looper.getMainLooper());

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;

                            int progress = (int) ((totalBytesRead * 100) / fileLength);
                            handler.post(() -> progressBar.setProgress(progress));
                        }
                    }

                    connection.disconnect();

                    requireActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        installApk(apkFile);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        progressDialog.dismiss();
                        try {
                            Toast.makeText(requireActivity(), getString(R.string.download_failed) + connection.getResponseMessage(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireActivity(), getString(R.string.download_failed) + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void installApk(File apkFile) {
        Uri apkUri;
        apkUri = androidx.core.content.FileProvider.getUriForFile(
            requireContext(),
            requireContext().getPackageName() + ".provider",
            apkFile
        );
        Log.d("InstallApk", "APK URI: " + apkUri.toString());

        boolean canInstall = requireContext().getPackageManager().canRequestPackageInstalls();
        if (!canInstall) {
            Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse("package:" + requireContext().getPackageName()));
            startActivity(settingsIntent);
            return;
        }

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (installIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(installIntent);
        } else {
            Toast.makeText(requireContext(), getString(R.string.unable_to_install), Toast.LENGTH_LONG).show();
        }
    }
}
