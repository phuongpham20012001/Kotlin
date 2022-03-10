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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelStoreOwner
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
    val userVM = viewModel<UserViewModel>()

    if(userVM.username.value.isEmpty()){
        Login(userVM)
    } else {
        MainScaffoldView()
    }


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
        .background(Color(0xFF5DC0EC))
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
        .background(Color(0xFF5DC0EC)),
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
            Column (){
                historyList.forEach{
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = it, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(20.dp))
                    AsyncImage(
                        model = "https://countryflagsapi.com/png/${it}",
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                    )
                }

            }

        } else {
            Text(text = " You have not searched anything yet", fontSize = 18.sp)
        }



    }
}

@Composable
fun HomeView( ) {
    var isHidden by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf( "")}
    var zzz by remember { mutableStateOf(false) }
    val countryVM = viewModel<CountryViewModel> (LocalContext.current as ViewModelStoreOwner)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFFFFF))
        ,
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
                    text = "Search for flag",
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
                        countryVM.getCountry(search)
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
            modifier = Modifier.background(Color(0xFF33A1D3))
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
                    modifier = Modifier.size(125.dp)
                )
                Text(text = "Capital: ${countryVM.capital.value}")
                Text(text = "Population: ${countryVM.population.value}" )
                Text(text = "Area: ${countryVM.area.value}")
                Text(text = " Continent: ${countryVM.continents.value}")
                Text(text = "Timezone: ${countryVM.timezones.value}")
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
            .fillMaxSize()
            .background(Color.White)
            .padding(0.dp, 30.dp,0.dp,0.dp)
        ,


        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "https://upload.wikimedia.org/wikipedia/commons/1/11/Flag_of_the_United_Nations.png",
            contentDescription = "",
            modifier = Modifier.size(150.dp)
        )
        Text(text = "Country app", fontSize = 50.sp)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") }
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = pw,
            onValueChange = { pw = it },
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(onClick = {
            userVM.loginUser(email, pw)
        }) {
            Text(text = "Log in")

        }

    }
}