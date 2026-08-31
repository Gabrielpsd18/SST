import '../entities/capacitacion_entity.dart';
import '../repositories/capacitaciones_repository.dart';

class GetCapacitacionesUseCase {
  final CapacitacionesRepository repository;

  GetCapacitacionesUseCase(this.repository);

  Future<PaginatedCapacitaciones> execute({
    String filtro = 'TODOS',
    int page = 0,
    int size = 10,
  }) {
    return repository.getCapacitaciones(
      filtro: filtro,
      page: page,
      size: size,
    );
  }
}
