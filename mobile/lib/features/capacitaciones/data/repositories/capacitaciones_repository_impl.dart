import '../../domain/entities/capacitacion_entity.dart';
import '../../domain/repositories/capacitaciones_repository.dart';
import '../datasources/capacitaciones_remote_data_source.dart';

class CapacitacionesRepositoryImpl implements CapacitacionesRepository {
  final CapacitacionesRemoteDataSource remoteDataSource;

  CapacitacionesRepositoryImpl({CapacitacionesRemoteDataSource? remoteDataSource})
      : remoteDataSource = remoteDataSource ?? CapacitacionesRemoteDataSourceImpl();

  @override
  Future<PaginatedCapacitaciones> getCapacitaciones({
    String filtro = 'TODOS',
    int page = 0,
    int size = 10,
  }) {
    return remoteDataSource.getCapacitaciones(
      filtro: filtro,
      page: page,
      size: size,
    );
  }
}
