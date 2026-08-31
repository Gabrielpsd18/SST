import 'package:flutter/material.dart';
import '../../domain/entities/user_entity.dart';
import '../../domain/repositories/auth_repository.dart';
import '../../domain/usecases/login_usecase.dart';
import '../../data/repositories/auth_repository_impl.dart';

enum AuthStatus {
  initial,
  authenticating,
  authenticated,
  unauthenticated,
  error,
}

class AuthProvider extends ChangeNotifier {
  final AuthRepository _repository;
  final LoginUseCase _loginUseCase;

  AuthStatus _status = AuthStatus.initial;
  UserEntity? _currentUser;
  String? _errorMessage;

  AuthProvider({
    AuthRepository? repository,
  })  : _repository = repository ?? AuthRepositoryImpl(),
        _loginUseCase = LoginUseCase(repository ?? AuthRepositoryImpl());

  AuthStatus get status => _status;
  UserEntity? get currentUser => _currentUser;
  String? get errorMessage => _errorMessage;
  bool get isAuthenticated => _status == AuthStatus.authenticated && _currentUser != null;
  bool get isLoading => _status == AuthStatus.authenticating;

  // Verifica si ya hay una sesión guardada en el dispositivo al iniciar la app
  Future<void> checkAuthStatus() async {
    try {
      final savedUser = await _repository.getSavedSession();
      if (savedUser != null && savedUser.isTrabajador) {
        _currentUser = savedUser;
        _status = AuthStatus.authenticated;
      } else {
        _currentUser = null;
        _status = AuthStatus.unauthenticated;
      }
    } catch (_) {
      _currentUser = null;
      _status = AuthStatus.unauthenticated;
    }
    notifyListeners();
  }

  // Realiza el login del trabajador
  Future<bool> login(String email, String password) async {
    _status = AuthStatus.authenticating;
    _errorMessage = null;
    notifyListeners();

    try {
      final user = await _loginUseCase.execute(email, password);
      _currentUser = user;
      _status = AuthStatus.authenticated;
      _errorMessage = null;
      notifyListeners();
      return true;
    } catch (e) {
      _status = AuthStatus.error;
      _errorMessage = e.toString().replaceAll('Exception: ', '').replaceAll('StateError: ', '').replaceAll('ArgumentError: ', '');
      notifyListeners();
      return false;
    }
  }

  // Cierra la sesión
  Future<void> logout() async {
    await _repository.logout();
    _currentUser = null;
    _errorMessage = null;
    _status = AuthStatus.unauthenticated;
    notifyListeners();
  }

  void clearError() {
    if (_errorMessage != null) {
      _errorMessage = null;
      notifyListeners();
    }
  }
}
