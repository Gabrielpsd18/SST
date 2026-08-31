import 'package:dio/dio.dart';
import '../../../../core/constants/api_constants.dart';
import '../../../../core/network/dio_client.dart';
import '../../domain/entities/capacitacion_entity.dart';

abstract class CapacitacionesRemoteDataSource {
  Future<PaginatedCapacitaciones> getCapacitaciones({
    required String filtro,
    required int page,
    required int size,
  });
}

class CapacitacionesRemoteDataSourceImpl implements CapacitacionesRemoteDataSource {
  final DioClient dioClient;

  CapacitacionesRemoteDataSourceImpl({DioClient? client})
      : dioClient = client ?? DioClient();

  @override
  Future<PaginatedCapacitaciones> getCapacitaciones({
    required String filtro,
    required int page,
    required int size,
  }) async {
    try {
      final response = await dioClient.dio.get(
        ApiConstants.capacitacionesMobile,
        queryParameters: {
          'filtro': filtro,
          'page': page,
          'size': size,
          'sort': 'fechaProgramada,desc',
        },
      );

      if (response.statusCode == 200) {
        final data = response.data;
        if (data is Map<String, dynamic>) {
          return PaginatedCapacitaciones.fromJson(data);
        }
      }

      throw Exception('Respuesta inesperada del servidor al listar capacitaciones');
    } on DioException catch (e) {
      if (e.response?.data is Map<String, dynamic>) {
        final msg = e.response?.data['message'] ?? e.response?.data['error'];
        if (msg != null) throw Exception(msg);
      }
      throw Exception('Error al cargar capacitaciones: ${e.message ?? 'Verifica tu conexión'}');
    } catch (e) {
      throw Exception(e.toString().replaceAll('Exception: ', ''));
    }
  }
}
