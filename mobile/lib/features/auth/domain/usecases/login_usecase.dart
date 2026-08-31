import '../entities/user_entity.dart';
import '../repositories/auth_repository.dart';

class LoginUseCase {
  final AuthRepository repository;

  LoginUseCase(this.repository);

  Future<UserEntity> execute(String email, String password) async {
    final cleanEmail = email.trim();
    final cleanPassword = password.trim();

    if (cleanEmail.isEmpty) {
      throw ArgumentError('Por favor ingresa tu correo o DNI');
    }
    if (cleanPassword.isEmpty) {
      throw ArgumentError('Por favor ingresa tu contraseña');
    }

    final user = await repository.login(cleanEmail, cleanPassword);

    if (!user.isTrabajador) {
      await repository.logout();
      throw StateError('Acceso restringido: Esta aplicación es exclusiva para trabajadores.');
    }

    return user;
  }
}
