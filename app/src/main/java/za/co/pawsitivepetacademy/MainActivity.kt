package za.co.pawsitivepetacademy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import java.util.Locale
import java.util.UUID

private val Forest=Color(0xFF164D43)
private val Sage=Color(0xFFBBD7BB)
private val Cream=Color(0xFFF8F5EB)
private val Muted=Color(0xFF52766A)
private val NameRegex=Regex("^[\\p{L}][\\p{L} '-]{1,69}$")
private val PhoneRegex=Regex("^(?:\\+27|0)[1-9][0-9]{8}$")
private const val ACADEMY_EMAIL="magogodigorata63@gmail.com"
private const val ACADEMY_PHONE="0787513255"
private const val MAP_QUERY="Durban, South Africa" // Replace with verified street address
private data class Course(val id:Int,val title:String,val category:String,val fee:Int,val months:Boolean,val description:String,val image:Int,val modules:List<String>)
private val courses=listOf(
 Course(1,"Canine Obedience Training","Behaviour",1500,true,"Build trust with positive reinforcement and everyday commands.",R.drawable.training,listOf("Understanding dog behaviour","Basic commands","Socialisation","Advanced techniques")),
 Course(2,"Pet Grooming","Everyday care",1500,true,"Learn safe grooming techniques for healthy coats and comfortable pets.",R.drawable.cat,listOf("Grooming tools","Bathing and brushing","Coat and nail care","Practical grooming")),
 Course(3,"Pet First Aid","Health & safety",750,false,"Learn calm, practical responses to common pet emergencies.",R.drawable.aid,listOf("Safety basics","Recognising emergencies","First response","Veterinary referral")),
 Course(4,"Puppy Care","Early learning",750,false,"Learn everyday puppy care, development and gentle socialisation.",R.drawable.dogs,listOf("Puppy nutrition","Daily routines","Socialisation","Basic puppy training")),
 Course(5,"Pet Business Management","Business",1500,true,"Build practical skills to plan and manage a pet-care business.",R.drawable.aid,listOf("Business planning","Customer service","Pricing","Operations")),
 Course(6,"Basic Dog Walking","Dog walking",750,false,"Learn safe handling, leash skills and responsible dog walking.",R.drawable.walking,listOf("Equipment","Leash handling","Route safety","Walking practice")),
 Course(7,"Animal Behaviour","Behaviour",1500,true,"Understand animal behaviour and encourage positive habits.",R.drawable.training,listOf("Animal communication","Behaviour patterns","Enrichment","Positive reinforcement"))
)
private fun money(v:Double)="R"+String.format(Locale.US,"%,.2f",v)
private fun tier(n:Int)=when {n>=4->.15;n==3->.10;n==2->.05;else->0.0}
private fun validName(v:String)=NameRegex.matches(v.trim()) && v.trim().any { it.isLetter() }
private fun validPhone(v:String)=PhoneRegex.matches(v.replace(" ","").replace("-",""))
private fun validEmail(v:String)=v.length<=254 && Patterns.EMAIL_ADDRESS.matcher(v.trim()).matches()
private class Store(ctx:Context){
 private val p=ctx.getSharedPreferences("pawsitive",Context.MODE_PRIVATE)
 var name by mutableStateOf(p.getString("name","")?:""); private set
 var email by mutableStateOf(p.getString("email","")?:""); private set
 var phone by mutableStateOf(p.getString("phone","")?:""); private set
 var reference by mutableStateOf(p.getString("reference","")?:""); private set
 var payment by mutableStateOf(p.getString("payment","Pending")?:"Pending"); private set
 var notify by mutableStateOf(p.getBoolean("notify",true))
 val enrolled=mutableStateListOf<Int>()
 val modules=mutableStateMapOf<String,Boolean>()
 init { try{val a=JSONArray(p.getString("enrolled","[]"));for(i in 0 until a.length())enrolled.add(a.getInt(i))}catch(_:Exception){}; try{val a=JSONArray(p.getString("modules","[]"));for(i in 0 until a.length())modules[a.getString(i)]=true}catch(_:Exception){} }
 fun profile(n:String,e:String,t:String){name=n;email=e;phone=t;p.edit().putString("name",n).putString("email",e).putString("phone",t).apply()}
 fun enrol(ids:List<Int>){ids.forEach{if(it !in enrolled)enrolled.add(it)};reference="PPA-"+UUID.randomUUID().toString().take(8).uppercase();payment="Pending";p.edit().putString("enrolled",JSONArray(enrolled.toList()).toString()).putString("reference",reference).putString("payment",payment).apply()}
 fun toggle(id:Int,index:Int){val key="$id:$index";if(modules[key]==true)modules.remove(key)else modules[key]=true;p.edit().putString("modules",JSONArray(modules.keys.toList()).toString()).apply()}
 fun progress(c:Course)=c.modules.indices.count{modules["${c.id}:$it"]==true}.toFloat()/c.modules.size
 fun updateNotifications(v:Boolean){notify=v;p.edit().putBoolean("notify",v).apply()}
}
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);val store=Store(this);setContent{Academy(store)}}}
private fun openEmail(ctx:Context,to:String,subject:String,body:String):Boolean{
 if(to.isBlank())return false
 return try { val intent=Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+Uri.encode(to))).apply{putExtra(Intent.EXTRA_SUBJECT,subject);putExtra(Intent.EXTRA_TEXT,body)};ctx.startActivity(Intent.createChooser(intent,"Choose email app"));true }catch(_:Exception){false}
}
private fun openMap(ctx:Context):Boolean=try{ctx.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+Uri.encode(MAP_QUERY))));true}catch(_:Exception){false}
private fun openPhone(ctx:Context):Boolean=if(ACADEMY_PHONE.isBlank())false else try{ctx.startActivity(Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+ACADEMY_PHONE)));true}catch(_:Exception){false}
@Composable private fun Academy(s:Store){
 var page by remember{mutableStateOf("Home")};var detail by remember{mutableIntStateOf(1)};var selected by remember{mutableStateOf(setOf<Int>())};var notice by remember{mutableStateOf("")};var menu by remember{mutableStateOf(false)}
 fun go(p:String){page=p;menu=false;notice=""}
 MaterialTheme(colorScheme=lightColorScheme(primary=Forest,secondary=Sage,background=Cream,surface=Color.White,onSurface=Forest)){
 Scaffold(containerColor=Cream,topBar={Surface(color=Color.White){Row(Modifier.fillMaxWidth().height(58.dp).padding(horizontal=16.dp),verticalAlignment=Alignment.CenterVertically){Image(painterResource(R.drawable.academy_logo),"Pawsitive Pet Academy logo",Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)));Spacer(Modifier.width(7.dp));Text("Pawsitive Pet Academy",fontWeight=FontWeight.Bold,color=Forest,fontSize=15.sp,modifier=Modifier.weight(1f));IconButton(onClick={menu=!menu}){Icon(if(menu)Icons.Default.Close else Icons.Default.Menu,"Menu",tint=Forest)}}}},bottomBar={if(!menu && page !in listOf("Registration","Success")) NavigationBar(containerColor=Color.White){listOf("Home","My Courses","Profile","Contact").forEach{p->NavigationBarItem(selected=page==p || (p=="My Courses"&&page=="Progress"),onClick={go(p)},icon={Icon(when(p){"Home"->Icons.Default.Home;"My Courses"->Icons.Default.MenuBook;"Profile"->Icons.Default.Person;else->Icons.Default.Chat},null)},label={Text(if(p=="Contact")"Support" else p,fontSize=10.sp)})}}}){pad->
 Column(Modifier.fillMaxSize().padding(pad)){
 if(notice.isNotBlank())Text(notice,Modifier.fillMaxWidth().background(Sage).padding(10.dp),color=Forest)
 if(menu){Page{Heading("EXPLORE THE ACADEMY");listOf("Home","About Us","Courses","Calculate Fees","My Courses","Profile","Contact").forEach{p->Surface(color=if(p==page)Sage else Color.White,shape=RoundedCornerShape(10.dp),modifier=Modifier.fillMaxWidth().clickable{go(p)}){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text(p,Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Icon(Icons.Default.ChevronRight,null)}}};Text("Practical knowledge. Compassionate care.");Action("Continue my learning"){go("My Courses")}}}
 else when(page){
 "Home"->Page{Hero(R.drawable.dogs);Heading("Welcome to Pawsitive Pet Academy");Text("Expert pet-care education for everyone.");Action("View Courses"){go("Courses")};Heading("Featured Courses");courses.take(3).forEach{CourseCard(it){detail=it.id;go("Details")}};Heading("Good learning. Better care.");Info("Compassion first","Reward-based training and thoughtful care that put your pet's wellbeing first.");Info("Practical by design","Clear lessons and useful activities you can bring into your everyday routine.");Info("Your pace, your place","Learn online around your schedule.");Info("Pawsitive Pet Academy","Durban, South Africa • Established 2023")}
 "About Us"->Page{Heading("About Us");Text("Better knowledge. Happier pets.");Hero(R.drawable.dogs);Info("Our History","Pawsitive Pet Academy was established in 2023 in Durban, South Africa by Sarah Smith to provide training courses for pet owners and aspiring pet-care professionals.");Info("Our Vision","Quality pet-care education for all.");Info("Our Mission","Empower pet owners with practical and compassionate knowledge, helping them give their animals thoughtful, responsible care.");Info("Our Goals","Accessible courses, better animal welfare and confident pet owners.")}
 "Courses"->LazyColumn(contentPadding=PaddingValues(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){item{Heading("Our Courses");Text("Choose from 7 expert-led pet-care courses.")};items(courses){c->CourseCard(c){detail=c.id;go("Details")}}}
 "Details"->Page{val c=courses.first{it.id==detail};Hero(c.image);Text(c.category,color=Muted);Heading(c.title);Text("${if(c.months)"6 months" else "6 weeks"} • Self-paced • Online");Heading(money(c.fee.toDouble()));Info("Course Purpose",c.description);Info("Course Content",c.modules.mapIndexed{index,m->"${index+1}. $m"}.joinToString("\n"));Action("Register"){selected=selected+c.id;go("Calculate Fees")};Outline("Back to Courses"){go("Courses")}}
 "Calculate Fees"->Quote(selected,{id,on->selected=if(on)selected+id else selected-id},{go("Registration")})
 "Registration"->Registration(s,selected.toList(),{go("Calculate Fees")},{go("Success")})
 "Success"->Page{Icon(Icons.Default.CheckCircle,null,tint=Sage,modifier=Modifier.size(65.dp));Heading("Registration Successful!");Text("Your registration has been recorded on this device. Payment is pending; no payment has been collected.");Info("Your registration","${s.name}\n${s.email}\nRegistration reference: ${s.reference}\nPayment status: ${s.payment}");Action("View My Courses"){go("My Courses")};Outline("Back to Home"){go("Home")};Hero(R.drawable.dogs)}
 "My Courses"->Page{Heading("My Courses");Text("Keep growing your pet-care confidence.");if(s.enrolled.isEmpty()){Info("No courses yet","Explore our courses and register to begin.");Action("Explore Courses"){go("Courses")}}else courses.filter{it.id in s.enrolled}.forEach{c->Surface(color=Color.White,shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){Hero(c.image);Text(c.title,fontWeight=FontWeight.Bold);val v=s.progress(c);LinearProgressIndicator(progress={v},modifier=Modifier.fillMaxWidth(),color=Forest,trackColor=Sage);Text("Overall progress: ${(v*100).toInt()}%");Action("Continue Course"){detail=c.id;go("Progress")}}}}}
 "Progress"->Page{val c=courses.first{it.id==detail};Heading(c.title);Text("Your learning journey");Hero(c.image);val v=s.progress(c);Info("Course Progress","Overall progress: ${(v*100).toInt()}% • ${c.modules.indices.count{s.modules["${c.id}:$it"]==true}} of ${c.modules.size} modules completed");LinearProgressIndicator(progress={v},modifier=Modifier.fillMaxWidth(),color=Forest,trackColor=Sage);c.modules.forEachIndexed{index,m->Surface(color=if(s.modules["${c.id}:$index"]==true)Sage else Color.White,shape=RoundedCornerShape(12.dp)){Row(Modifier.fillMaxWidth().padding(14.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(checked=s.modules["${c.id}:$index"]==true,onCheckedChange={s.toggle(c.id,index)});Column{Text("Module ${index+1}: $m",fontWeight=FontWeight.Bold);Text(if(s.modules["${c.id}:$index"]==true)"Completed" else "Tap to mark complete",fontSize=12.sp)}}}};Action("Continue Learning"){notice="Select a module checkbox to record your progress."}}
 "Profile"->Profile(s,{notice=it})
 "Contact"->Contact({notice=it})
 }
 }
 }
 }
}
@Composable private fun Page(content:@Composable ColumnScope.()->Unit){Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)}
@Composable private fun Heading(v:String){Text(v,color=Forest,fontWeight=FontWeight.Bold,fontSize=23.sp)}
@Composable private fun Action(v:String,enabled:Boolean=true,click:()->Unit){Button(onClick=click,enabled=enabled,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(9.dp),colors=ButtonDefaults.buttonColors(containerColor=Forest)){Text(v)}}
@Composable private fun Outline(v:String,click:()->Unit){OutlinedButton(onClick=click,modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(9.dp)){Text(v)}}
@Composable private fun Info(title:String,body:String){Surface(color=Color.White,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(15.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text(title,color=Forest,fontWeight=FontWeight.Bold);Text(body,color=Forest,fontSize=13.sp,lineHeight=19.sp)}}}
@Composable private fun Hero(id:Int){Image(painterResource(id),null,Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(12.dp)),contentScale=ContentScale.Crop)}
@Composable private fun CourseCard(c:Course,click:()->Unit){Surface(color=Color.White,shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){Hero(c.image);Text(c.category,color=Muted,fontSize=12.sp);Text(c.title,fontWeight=FontWeight.Bold,color=Forest,fontSize=18.sp);Text(c.description,fontSize=13.sp);Text("${money(c.fee.toDouble())} • ${if(c.months)"6 months" else "6 weeks"}",fontWeight=FontWeight.Bold);Outline("View Course"){click()}}}}
@Composable private fun Field(label:String,value:String,change:(String)->Unit,keyboard:KeyboardType=KeyboardType.Text,limit:Int=100,error:String?=null){OutlinedTextField(value=value,onValueChange={if(it.length<=limit)change(it)},label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=keyboard),isError=error!=null,supportingText={if(error!=null)Text(error)})}
@Composable private fun Quote(selected:Set<Int>,change:(Int,Boolean)->Unit,continueClick:()->Unit){var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var phone by remember{mutableStateOf("")};var loyalty by remember{mutableStateOf(false)};var error by remember{mutableStateOf("")};val subtotal=courses.filter{it.id in selected}.sumOf{it.fee}.toDouble();val d=tier(selected.size);val loyaltyDiscount=if(loyalty)0.05 else 0.0;val afterTier=subtotal*(1-d);val total=afterTier*(1-loyaltyDiscount)
 Page{Heading("Calculate Fees");Text("Plan your learning. See your savings.");Info("Customer Details","Enter your details below. These fields are for a quotation only.");Field("Full Name",name,{name=it},limit=70);Field("Email Address",email,{email=it},KeyboardType.Email,254);Field("Phone Number",phone,{phone=it.filter{ch->ch.isDigit()||ch=='+'||ch==' '}.take(16)},KeyboardType.Phone,16);Surface(color=Color.White,shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(14.dp)){Text("Select Courses",fontWeight=FontWeight.Bold);courses.forEach{c->Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=c.id in selected,onCheckedChange={change(c.id,it)});Text(c.title,Modifier.weight(1f),fontSize=12.sp);Text(money(c.fee.toDouble()),fontSize=12.sp)}}}};Surface(color=Sage,shape=RoundedCornerShape(14.dp)){Column(Modifier.padding(14.dp)){Text("Discount Information",fontWeight=FontWeight.Bold);Text("2 courses: 5% • 3 courses: 10% • 4+ courses: 15%",fontSize=12.sp);Row(verticalAlignment=Alignment.CenterVertically){Checkbox(loyalty,{loyalty=it});Text("Apply loyalty discount (extra 5%)",fontSize=12.sp)};Text("Loyalty discount is a demo rule and requires eligibility verification.",fontSize=11.sp)}};Info("Fee Summary","Subtotal: ${money(subtotal)}\nCourse discount (${(d*100).toInt()}%): -${money(subtotal*d)}\nLoyalty discount: -${money(afterTier*loyaltyDiscount)}\nTotal Fee: ${money(total)}");if(error.isNotBlank())Text(error,color=Color.Red);Action("Continue",selected.isNotEmpty()){error=when{!validName(name)->"Enter a valid full name (2–70 characters).";!validEmail(email)->"Enter a valid email address.";!validPhone(phone)->"Enter a valid South African phone number.";else->""};if(error.isBlank())continueClick()};Outline("Clear"){courses.forEach{change(it.id,false)};name="";email="";phone="";loyalty=false}}
}
@Composable private fun Registration(s:Store,ids:List<Int>,back:()->Unit,done:()->Unit){var name by remember{mutableStateOf(s.name)};var email by remember{mutableStateOf(s.email)};var phone by remember{mutableStateOf(s.phone)};var experience by remember{mutableStateOf("Beginner")};var payment by remember{mutableStateOf("Online – self-paced")};var terms by remember{mutableStateOf(false)};var error by remember{mutableStateOf("")};val chosen=courses.filter{it.id in ids};val sub=chosen.sumOf{it.fee}.toDouble();val total=sub*(1-tier(ids.size))
 Page{Heading("Course Registration");Text("One step closer to better pet care.");Info("Registration summary",chosen.joinToString("\n"){it.title}+"\n${ids.size} course(s) • ${money(total)} • Payment pending");Heading("Student details");Field("Full name",name,{name=it},limit=70);Field("Email",email,{email=it},KeyboardType.Email,254);Field("Phone number",phone,{phone=it.filter{ch->ch.isDigit()||ch=='+'||ch==' '}.take(16)},KeyboardType.Phone,16);Text("Preferred learning option");listOf("Online – self-paced","Contact me about classes").forEach{v->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=payment==v,onClick={payment=v});Text(v)}};Text("Experience level");listOf("Beginner","Intermediate","Advanced").forEach{v->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=experience==v,onClick={experience=v});Text(v)}};Row(verticalAlignment=Alignment.CenterVertically){Checkbox(terms,{terms=it});Text("I agree to the Terms and Conditions and local data storage for this demo.",fontSize=12.sp)};if(error.isNotBlank())Text(error,color=Color.Red);Action("Complete Registration",ids.isNotEmpty()){error=when{!validName(name)->"Enter a valid full name.";!validEmail(email)->"Enter a valid email address.";!validPhone(phone)->"Enter a valid South African phone number.";!terms->"You must agree before registering.";else->""};if(error.isBlank()){s.profile(name.trim(),email.trim(),phone.trim());s.enrol(ids);done()}};Outline("Cancel"){back()}}
}
@Composable private fun Profile(s:Store,notify:(String)->Unit){var editing by remember{mutableStateOf(false)};var name by remember(s.name){mutableStateOf(s.name)};var email by remember(s.email){mutableStateOf(s.email)};var phone by remember(s.phone){mutableStateOf(s.phone)};var error by remember{mutableStateOf("")}
 Page{Heading("My Profile");Text("Your details and learning preferences.");Info(s.name.ifBlank{"Student profile"},s.email+"\n"+s.phone);Info("Personal information","${s.name}\n${s.email}\n${s.phone}\nLearning option: Online – Self-paced");Info("Learning overview","Enrolled courses: ${s.enrolled.size}\nCompleted courses: ${courses.count{it.id in s.enrolled && s.progress(it)==1f}}");Surface(color=Color.White,shape=RoundedCornerShape(12.dp)){Column(Modifier.padding(12.dp)){Text("Account settings",fontWeight=FontWeight.Bold);Row(verticalAlignment=Alignment.CenterVertically){Checkbox(s.notify,{s.updateNotifications(it)});Text("Enroll me in learning reminders")};Text("Reminder preference is stored locally; no push notifications are sent.",fontSize=11.sp)}};if(editing){Field("Full name",name,{name=it},limit=70);Field("Email",email,{email=it},KeyboardType.Email,254);Field("Phone",phone,{phone=it},KeyboardType.Phone,16);if(error.isNotBlank())Text(error,color=Color.Red);Action("Save profile"){error=when{!validName(name)->"Enter a valid full name.";!validEmail(email)->"Enter a valid email.";!validPhone(phone)->"Enter a valid South African phone number.";else->""};if(error.isBlank()){s.profile(name.trim(),email.trim(),phone.trim());editing=false;notify("Profile saved on this device.")}}}else Action("Edit Profile"){editing=true};Outline("Logout (clear session)"){notify("This offline demo has no login session. Your locally saved data remains available.")}}
}
@Composable private fun Contact(notify:(String)->Unit){val ctx=LocalContext.current;var name by remember{mutableStateOf("")};var email by remember{mutableStateOf("")};var subject by remember{mutableStateOf("Help with course registration")};var message by remember{mutableStateOf("")};var error by remember{mutableStateOf("")}
 Page{Heading("Contact Us");Text("A friendly team, here to help.");Hero(R.drawable.cat);Text("Get in touch with Pawsitive Pet Academy",fontSize=12.sp,color=Muted);Surface(color=Color.White,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().clickable{if(!openPhone(ctx))notify("A verified phone number has not been configured.")}){Column(Modifier.padding(14.dp)){Text("Phone",fontWeight=FontWeight.Bold);Text(ACADEMY_PHONE.ifBlank{"Number awaiting verification"})}};Surface(color=Color.White,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().clickable{if(!openEmail(ctx,ACADEMY_EMAIL,"Pawsitive Pet Academy enquiry",""))notify("Please configure the verified academy email address first.")}){Column(Modifier.padding(14.dp)){Text("Email",fontWeight=FontWeight.Bold);Text(ACADEMY_EMAIL.ifBlank{"Email awaiting verification"})}};Surface(color=Color.White,shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth().clickable{if(!openMap(ctx))notify("No maps app or browser available.")}){Column(Modifier.padding(14.dp)){Text("Location — Open directions",fontWeight=FontWeight.Bold);Text(MAP_QUERY)}};Info("Opening hours","Opening hours will be confirmed by the academy");Heading("Send us a message");Field("Name",name,{name=it},limit=70);Field("Email",email,{email=it},KeyboardType.Email,254);Text("Subject");listOf("Help with course registration","Payment question","Course information","Other enquiry").forEach{v->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=subject==v,onClick={subject=v});Text(v,fontSize=12.sp)}};OutlinedTextField(value=message,onValueChange={if(it.length<=1000)message=it},label={Text("Message (10–1000 characters)")},minLines=4,modifier=Modifier.fillMaxWidth());if(error.isNotBlank())Text(error,color=Color.Red);Action("Send Message"){error=when{!validName(name)->"Enter a valid name (2–70 characters).";!validEmail(email)->"Enter a valid email address.";message.trim().length !in 10..1000->"Message must contain 10–1000 characters.";ACADEMY_EMAIL.isBlank()->"The verified academy email address must be configured before email can open.";else->""};if(error.isBlank()){val body="From: ${name.trim()}\nReply email: ${email.trim()}\n\n${message.trim()}";if(!openEmail(ctx,ACADEMY_EMAIL,subject,body))notify("No email application is available. Install or configure an email app.")}};Info("Need a little guidance?","Find answers about registration, payment, course access and certificates.")}
}
