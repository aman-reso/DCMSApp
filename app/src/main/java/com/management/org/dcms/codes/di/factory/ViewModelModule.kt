package com.management.org.dcms.codes.di.factory//package com.management.org.dcms.codes.di.factory
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.management.org.dcms.codes.viewmodel.DcmsMainViewModel
//import com.management.org.dcms.codes.viewmodel.logintvm.LoginViewModel
//import dagger.Binds
//import dagger.Module
//import dagger.multibindings.IntoMap
//
//@Module
//internal abstract class ViewModelModule {
//
//    @Binds
//    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
//
//
//    /*
//     * This method basically says
//     * inject this object into a Map using the @IntoMap annotation,
//     * with the  LoginViewModel.class as key,
//     * and a Provider that will build a MovieListViewModel
//     * object.
//     *
//     * */
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(DcmsMainViewModel::class)
//    protected abstract fun getDcmsMainViewModel(viewModel: DcmsMainViewModel): ViewModel
//
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(LoginViewModel::class)
//    protected abstract fun getLoginViewModel(viewModel: LoginViewModel): ViewModel
//
//
//}