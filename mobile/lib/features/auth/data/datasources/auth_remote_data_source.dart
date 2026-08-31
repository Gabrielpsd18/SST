import 'package:dio/dio.dart';
import '../../../../core/constants/api_constants.dart';
import '../../../../core/network/dio_client.dart';
import '../../domain/entities/user_entity.dart';

abstract class AuthRemoteDataSource {
  Future<UserEntity> login(String email, String password);
}

class AuthRemoteDataSourceImpl implements AuthRemoteDataSource {
  final DioClient dioClient;

  AuthRemoteDataSourceImpl({DioClient? client})
      : dioClient = client ?? DioClient();

  @override
  Future<UserEntity> login(String email, String password) async {
    try {
      final response = await dioClient.dio.post(
        ApiConstants.loginMobile,
        data: {
          'email': email,
          'password': password,
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final body = response.data;
        if (body is Map<String, dynamic>) {
          final data = body['data'] ?? body;
          return UserEntity.fromJson(data);
        }
      }

      throw Exception('Respuesta no válida del servidor');
    } on DioException catch (e) {
      String errorMessage = 'Error al conectar con el servidor';

      if (e.type == DioExceptionType.connectionTimeout ||
          e.type == DioExceptionType.receiveTimeout ||
          e.type == DioExceptionType.sendTimeout) {
        errorMessage = 'Tiempo de espera agotado. Verifica tu conexión a internet o el estado del servidor.';
      } else if (e.type == DioExceptionType.connectionError) {
        errorMessage = 'No se pudo establecer conexión con el backend (${dioClient.dio.options.baseUrl}). Verifica que esté en ejecución.';
      } else if (e.response != null) {
        final responseData = e.response?.data;
        if (responseData is Map<String, dynamic>) {
          errorMessage = responseData['message'] ??
              responseData['error'] ??
              'Credenciales incorrectas o error en el servidor (${e.response?.statusCode})';
        } else if (responseData is String && responseData.isNotEmpty) {
          errorMessage = responseData;
        } else {
          errorMessage = 'Error del servidor: Código ${e.response?.statusCode}';
        }
      }

      throw Exception(errorMessage);
    } catch (e) {
      throw Exception(e.toString().replaceAll('Exception: ', ''));
    }
  }
}
