package com.chepizhko.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.OnCheckedChangeListener;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mButtontStart, mButtonEnd, mButtonDelete;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;
    private int mCurrent;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mCurrent = CrimeLab.get(getActivity()).getCrimes().size();
        // сохранение местонахождения файла фотографии.
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Здесь намеренно оставлено пустое место
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // И здесь тоже
            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mButtontStart = (Button) v.findViewById(R.id.btn_start);
        mButtontStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewPager) getActivity().findViewById(R.id.crime_view_pager)).setCurrentItem(0);
            }
        });
        mButtonEnd = (Button) v.findViewById(R.id.btn_end);
        mButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewPager) getActivity().findViewById(R.id.crime_view_pager)).setCurrentItem(mCurrent);
            }
        });
        mButtonDelete = (Button) v.findViewById(R.id.btn_delete_crime);
        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("text/plain");
//                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                i = Intent.createChooser(i, getString(R.string.send_report));// создать список программ(показывать список всегда)
//                startActivity(i);
                  Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                          .getIntent()
                          .setAction(Intent.ACTION_SEND)
                          .setType("text/plain")
                          .putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                          .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                  intent = Intent.createChooser(intent, getString(R.string.send_report));// создать список программ(показывать список всегда)
                  startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        // PackageManager известно все о компонентах, установленных на устройстве Android, включая все его активности
        // Проверяем есть ли на устройстве приложение контактов
        PackageManager packageManager = getActivity().getPackageManager();
        // Вызывая resolveActivity(Intent, int), вы приказываете найти активность, соответствующую переданному интенту.
        // Флаг MATCH_DEFAULT_ONLY ограничивает поиск активностями с флагом CATEGORY_DEFAULT
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        // ACTION_CAPTURE_IMAGE послушно запускает приложение камеры и делает снимок,
        // но результат не является фотографией в полном разрешении
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // код, блокирующий кнопку при отсутствии приложения камеры или недоступности места, в котором должна сохраняться фотография
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Чтобы получить выходное изображение в высоком разрешении, необходимо сообщить,
                // где должно храниться изображение в файловой системе определяемом mPhotoFile
                // Вызов FileProvider.getUriForFile(…) преобразует локальный путь к файлу в объект Uri, «понятный» приложению камеры
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.chepizhko.criminalintent.fileprovider", mPhotoFile);
                // Но чтобы выполнить запись, необходимо установить флаг Intent.FLAG_GRANT_WRITE_URI_PERMISSION для каждой активности,
                // с которой может быть связан интент captureImage. Флаг предоставляет разрешение на запись для этого конкретного Uri
                // Включение атрибута android:grantUriPermissions в МАНИФЕСТ!!!
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                PhotoFragment dialog = PhotoFragment.newInstance(mPhotoFile);
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });
        updatePhotoView();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            // получаем дату
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            // URI данных — ссылка на конкретный контакт, выбранный пользователем
            Uri contactUri = data.getData();
            // Определение полей, значения которых должны быть возвращены запросом.
            // создается запрос на все отображаемые имена контактов из возвращаемых данных
            String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
            // Выполнение запроса - contactUri здесь выполняет функции условия "where"
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                // Проверка получения результатов
                if (cursor.getCount() == 0) {
                    return;
                }
                // Извлечение первого столбца данных - имени подозреваемого.
                cursor.moveToFirst();
                String suspect = cursor.getString(0);
                // Занесение имени подозреваемого в базу
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                cursor.close();
            }
        }else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.chepizhko.criminalintent.fileprovider", mPhotoFile);
            // Теперь, когда камера завершила запись в файл, можно отозвать разрешение и снова перекрыть доступ к файлу.
            getActivity().revokeUriPermission( uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    //метод, который создает четыре строки, соединяет их и возвращает полный отчет
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
    // Чтобы загрузить объект Bitmap в ImageView, добавьте в CrimeFragment метод для обновления mPhotoView
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap ( mPhotoFile.getPath(), getActivity() );
            mPhotoView.setImageBitmap(bitmap);
        }
    }

}
