import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../../domain/entities/user_entity.dart';
import '../../domain/repositories/auth_repository.dart';
import '../datasources/auth_remote_data_source.dart';

class AuthRepositoryImpl implements AuthRepository {
  final AuthRemoteDataSource remoteDataSource;
  final FlutterSecureStorage secureStorage;

  static const String _keyToken = 'jwt_token';
  static const String _keyUserData = 'user_data';

  AuthRepositoryImpl({
    AuthRemoteDataSource? remoteDataSource,
    FlutterSecureStorage? secureStorage,
  })  : remoteDataSource = remoteDataSource ?? AuthRemoteDataSourceImpl(),
        secureStorage = secureStorage ?? const FlutterSecureStorage();

  @override
  Future<UserEntity> login(String email, String password) async {
    final user = await remoteDataSource.login(email, password);

    // Guardar credenciales y token en almacenamiento seguro
    await secureStorage.write(key: _keyToken, value: user.accessToken);
    await secureStorage.write(key: _keyUserData, value: jsonEncode(user.toJson()));

    return user;
  }

  @override
  Future<void> logout() async {
    await secureStorage.delete(key: _keyToken);
    await secureStorage.delete(key: _keyUserData);
  }

  @override
  Future<UserEntity?> getSavedSession() async {
    try {
      final token = await secureStorage.read(key: _keyToken);
      final rawUserData = await secureStorage.read(key: _keyUserData);

      if (token == null || rawUserData == null) {
        return null;
      }

      final Map<String, dynamic> jsonMap = jsonDecode(rawUserData);
      return UserEntity.fromJson(jsonMap);
    } catch (_) {
      await logout();
      return null;
    }
  }

  @override
  Future<bool> isAuthenticated() async {
    final token = await secureStorage.read(key: _keyToken);
    return token != null && token.isNotEmpty;
  }
}
