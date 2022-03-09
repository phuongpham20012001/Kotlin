package com.example.firebase

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val HOME_ROUTE = "home"
const val SEARCH_ROUTE = "search"
@Composable
fun MainView() {
//    val userVM = viewModel<UserViewModel>()
//
//    if(userVM.username.value.isEmpty()){
//        Login(userVM)
//    } else {
//            MainScaffoldView()
//    }
    MainScaffoldView()

}


@Composable
fun MainScaffoldView() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBarView() },
        bottomBar = {BottomBarView (navController)},
        content = {MainContentView(navController)}
    )
}
@Composable
fun TopBarView(){
    val userVM = viewModel<UserViewModel>()
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Cyan)
        .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
        ) {
        Text(text = " Hello ${userVM.username.value}")
        OutlinedButton(onClick = {userVM.logoutUser()}) {
            Text(text = "Log Out")
            
        }
    }
}
@Composable
fun BottomBarView (navController : NavHostController){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Cyan),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_location_searching),
            contentDescription = "home",
            modifier = Modifier.clickable {  navController.navigate(HOME_ROUTE)  })
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_history_24),
            contentDescription = "note",
            modifier = Modifier.clickable {  navController.navigate(SEARCH_ROUTE)  })
    }
}
@Composable
fun MainContentView(navController: NavHostController) {


    NavHost(navController = navController, startDestination = HOME_ROUTE ){
        composable( route = HOME_ROUTE ){ HomeView() }
        composable( route = SEARCH_ROUTE){ SearchHistory () }
    }
}

@Composable
fun SearchHistory() {
    var historyList by remember {
        mutableStateOf(mutableListOf<String>())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,


        ){
        Text(text = "Search History", fontSize = 24.sp)
        val fireStore = Firebase.firestore
        fireStore.collection("History").get().addOnSuccessListener {
            var history = mutableListOf<String>()
            for (doc in it ){

                history.add( doc.get("search").toString())
                historyList = history
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        if(historyList.isNotEmpty() ) {
            historyList.forEach{
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = it, fontSize = 20.sp)
            }
        } else {
            Text(text = " You have not searched anything yet", fontSize = 18.sp)
        }



    }
}

@Composable
fun HomeView() {
    var isHidden by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf( "")}
    var zzz by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFFFFF))
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (!isHidden) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,


                ) {
                Text(
                    text = "Search Bar",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text(text = "Search") }
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    onClick = {
                    val fireStore = Firebase.firestore
                        fireStore.collection("History").add(SearchData(search))
                        zzz = !zzz
                    //search = ""

                    }
                ) {
                    Text(text = "Search")

                }
            }

        } else {
            Column {}
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.background(Color.Cyan)
        ) {
            Icon(
                painter = painterResource(if (!isHidden) R.drawable.ic_baseline_arrow_drop_up_24 else R.drawable.ic_baseline_arrow_drop_down),
                contentDescription = "",
                modifier = Modifier.clickable {
                    isHidden = !isHidden
                }
            )
        }

        Divider(thickness = 2.dp)
        if(zzz) {
            Column() {
                AsyncImage(
                    model = "https://countryflagsapi.com/png/${search}",
                    contentDescription = "",
                    modifier = Modifier.size(200.dp)
                )
            }
        } else {
            Column {}
        }
    }
}




        //Login stuff
        @Composable
        fun Login(userVM: UserViewModel) {

            var email by remember { mutableStateOf("") }
            var pw by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") }
                )
                OutlinedTextField(
                    value = pw,
                    onValueChange = { pw = it },
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedButton(onClick = {
                    userVM.loginUser(email, pw)
                }) {
                    Text(text = "Log in")

                }

            }
        }


