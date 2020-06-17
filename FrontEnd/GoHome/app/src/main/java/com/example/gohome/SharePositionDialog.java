package com.example.gohome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class SharePositionDialog extends DialogFragment {
    // key로 사용
    private static final String ARG_DIALOG_STRING = "dialog_msg";
    private static final String ARG_DIALOG_LINK = "dialog_link";

    // dialog에 표시되는 textview
    private TextView firstTextView;
    private TextView secondTextView;

    // 입력받은 데이터를 저장
    private String msg;
    private String link;

    private ClipboardManager clipboard;
    private Context context;

    public static SharePositionDialog newInstance(String link) {
        // bundle에 전달받은 데이터를 입력
        Bundle bundle = new Bundle();
        bundle.putString(ARG_DIALOG_LINK, link);

        SharePositionDialog fragment = new SharePositionDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if(getArguments() != null) {
            // bundle에서 입력한 데이터를 얻어 초기화
            this.msg = "내 위치를 가족/친구에게 공유하세요.\n(위치정보 제3자 제공에 동의합니다.)";
            this.link = getArguments().getString(ARG_DIALOG_LINK);
        }
        clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        context = (Context)getActivity();
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        // dialog_1 레이아웃의 View 객체 얻고, 원소 초기화
        View view = inflater.inflate(R.layout.dialog_confirm, null);
        firstTextView = view.findViewById(R.id.dialog_confirm_msg);
        firstTextView.setText(msg);

        builder.setView(view)
                .setTitle("위치 정보 공유")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 2번째 dialog 생성, 위치공유 링크를 표시
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        View view1 = inflater.inflate(R.layout.dialog_url, null);
                        secondTextView = view1.findViewById(R.id.dialog_link);
                        secondTextView.setText(link);
                        ImageButton imb = view1.findViewById(R.id.dialog_copy);
                        imb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // link를 클립보드에 복사
                                ClipData clip = ClipData.newPlainText("link copy", link);
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "복사되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder1.setView(view1)
                                .setTitle("위치 정보 공유")
                                .setPositiveButton("닫기", null)
                                .create().show();
                    }
                })
                .setNegativeButton("취소", null);
        return builder.create();
    }

}
