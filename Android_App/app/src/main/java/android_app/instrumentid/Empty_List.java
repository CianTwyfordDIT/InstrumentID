//package android_app.instrumentid;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//
//public class List_Empty extends Activity
//{
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.empty_list);
//
//        //Button to return to main menu if list is empty
//        Button returnToMain = findViewById(R.id.returnButton);
//        returnToMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
//                startActivity(intent);
//            }
//        });
//    }
//}
//
