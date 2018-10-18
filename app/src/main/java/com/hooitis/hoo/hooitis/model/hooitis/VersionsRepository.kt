package com.hooitis.hoo.hooitis.model.hooitis

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hooitis.hoo.hooitis.BuildConfig
import com.hooitis.hoo.hooitis.model.SharedPreferenceHelper
import com.hooitis.hoo.hooitis.utils.FIREBASE_VERSION_DB
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Suppress("unused")
class VersionsRepository @Inject constructor(private val versionsDao: VersionsDao,
//                                             private val foodRepository: FoodRepository,
//                                             private val firebaseStore: FirebaseFirestore,
                                             private val sharedPreferenceHelper: SharedPreferenceHelper){


    val mCompositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    init {
    }

//    private fun loadVersionFromServer(): Observable<Versions> {
//        return Observable.create { emitter ->
//            firebaseStore.collection(FIREBASE_VERSION_DB).document(FIREBASE_VERSION_DB).get().addOnCompleteListener { res ->
//                if (res.isSuccessful) {
//                    emitter.onNext(
//                            Versions(appVersion = res.result!!.data!!["appVersion"].toString().toLong()))
//                }else{
//                    emitter.onError(Exception())
//                }
//            }.addOnFailureListener {err ->
//                Log.d("error", err.toString())
//            }
//        }
//    }

    private fun loadLocalVersions(): Versions {
        var versionList = versionsDao.get()
        if(versionList.isEmpty()){
            versionsDao.insert(Versions(0, BuildConfig.VERSION_CODE.toLong()))
            // init application
            versionList = versionsDao.get()
        }

        return versionList.first()
    }

    fun saveVersion(versions: Versions): Observable<Versions> {
        versionsDao.insert(versions)
        return Observable.just(versions)
    }

    @SuppressLint("CheckResult")
    fun checkVersion(){
//        loadVersionFromServer()
//                .subscribe({serverVersion ->
//                    val localVersion = loadLocalVersions()
////                    if(serverVersion.foodDBVersion != localVersion.foodDBVersion){
////                        foodRepository.loadFoodsFromStore().subscribe({
////                        },{
////
////                        })
////                    }
//                    if(serverVersion.appVersion != localVersion.appVersion){
//                        // update application from store
//                    }
//                    // update version db
//                    versionsDao.deleteAll()
//                    versionsDao.insert(serverVersion)
//                }, {
//                })
    }
}
