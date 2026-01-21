package com.sodep.prueba_tecnica.dto;

public record ExternalUserDTO(
                Long id,
                String name,
                String username,
                String email,
                Address address,
                String phone,
                String website,
                Company company) {
        public record Address(
                        String street,
                        String suite,
                        String city,
                        String zipcode,
                        Geo geo) {
                public record Geo(String lat, String lng) {
                }
        }

        public record Company(
                        String name,
                        String catchPhrase,
                        String bs) {
        }

        // Convierte ExternalUserDTO a ClienteRequestDTO
        public ClienteRequestDTO toClienteRequest() {
                String direccionCompleta = address != null
                                ? String.format("%s %s, %s %s",
                                                address.street(),
                                                address.suite(),
                                                address.city(),
                                                address.zipcode())
                                : null;

                return new ClienteRequestDTO(
                                name,
                                email,
                                phone,
                                direccionCompleta,
                                String.valueOf(id));
        }
}
