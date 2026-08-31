import '../entities/capacitacion_entity.dart';

abstract class CapacitacionesRepository {
  Future<PaginatedCapacitaciones> getCapacitaciones({
    String filtro = 'TODOS', // 'TODOS', 'PENDIENTES', 'REALIZADAS'
    int page = 0,
    int size = 10,
  });
}
