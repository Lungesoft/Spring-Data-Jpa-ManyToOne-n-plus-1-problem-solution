Run Application

Problem: query type method (Criteria API, spring data find methods) ignores @Fetch(FetchMode.JOIN), but you can specify fetch behavior with EntityGraph objects.

See EntityGraph objects generation inside `com.lungesoft.spring.jpa.BaseEntityGraphsConfigurator` class.

Applying for Spring Data Jpa repository query methods (like findAll()) inside `com.lungesoft.spring.jpa.EntityGraphJpaRepository` class.

Applying example for Spring Data Jpa repository custom methods (like findByFirstName()) inside `com.lungesoft.spring.jpa.repositoty.CustomerRepository` class.


We have:
1. Data
    ```
    new Customer("Jack", "Bauer", new Address(new City("city1"), new Street("street1")));
    new Customer("Jack", "O'Brian", new Address(new City("city2"), new Street("street2")));
    new Customer("Jack", "Bauer", new Address(new City("city3"), new Street("street3")));
    new Customer("Jack", "Palmer", new Address(new City("city4"), new Street("street4")));
    new Customer("Jack", "Dessler", new Address(new City("city5"), new Street("street5")));
    ```
2. Entity with 2 ManyToOne field and 1 OneToMany field, that matched with @Fetch(FetchMode.SUBSELECT)  
    ```
    @Entity
    public class Address {
    
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
        @ManyToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "city_id", nullable = false)
        private City city;
        @ManyToOne(cascade = CascadeType.PERSIST)
        @JoinColumn(name = "street_id", nullable = false)
        private Street street;
    
        @Fetch(FetchMode.SUBSELECT)
        @OneToMany(mappedBy = "address")
        private List<Customer> customers;
    ```
3. Method, that calls addressRepository.findAll()
    ```
    @GetMapping("/findAll")
    public void findAll() {
        LOGGER.info("start addresses with findAll()");
        for (Address address : addressRepository.findAll()) {
            for (Customer customer : address.getCustomers()) {
                LOGGER.info(" - customer {} with that address ", customer.getFirstName());
            }
        }
        LOGGER.info("end addresses with findAll()");
    }
    ```
Go to http://localhost:8080/findAll.

You can see:
```
SORTLOG     : start addresses with findAll()
SORTLOG     : Using ASTQueryTranslatorFactory
Hibernate: select address0_.id as id1_0_0_, city1_.id as id1_1_1_, street2_.id as id1_3_2_, address0_.city_id as city_id2_0_0_, address0_.street_id as street_i3_0_0_, city1_.title as title2_1_1_, street2_.title as title2_3_2_ from address address0_ left outer join city city1_ on address0_.city_id=city1_.id left outer join street street2_ on address0_.street_id=street2_.id
Hibernate: select customers0_.address_id as address_4_2_1_, customers0_.id as id1_2_1_, customers0_.id as id1_2_0_, customers0_.address_id as address_4_2_0_, customers0_.first_name as first_na2_2_0_, customers0_.last_name as last_nam3_2_0_ from customer customers0_ where customers0_.address_id in (select address0_.id from address address0_ left outer join city city1_ on address0_.city_id=city1_.id left outer join street street2_ on address0_.street_id=street2_.id)
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     : end addresses with findAll()
```
instead:
```
SORTLOG     : start addresses with findAll()
SORTLOG     : Using ASTQueryTranslatorFactory
Hibernate: select address0_.id as id1_0_, address0_.city_id as city_id2_0_, address0_.street_id as street_i3_0_ from address address0_
Hibernate: select city0_.id as id1_1_0_, city0_.title as title2_1_0_ from city city0_ where city0_.id=?
Hibernate: select street0_.id as id1_3_0_, street0_.title as title2_3_0_ from street street0_ where street0_.id=?
Hibernate: select city0_.id as id1_1_0_, city0_.title as title2_1_0_ from city city0_ where city0_.id=?
Hibernate: select street0_.id as id1_3_0_, street0_.title as title2_3_0_ from street street0_ where street0_.id=?
Hibernate: select city0_.id as id1_1_0_, city0_.title as title2_1_0_ from city city0_ where city0_.id=?
Hibernate: select street0_.id as id1_3_0_, street0_.title as title2_3_0_ from street street0_ where street0_.id=?
Hibernate: select city0_.id as id1_1_0_, city0_.title as title2_1_0_ from city city0_ where city0_.id=?
Hibernate: select street0_.id as id1_3_0_, street0_.title as title2_3_0_ from street street0_ where street0_.id=?
Hibernate: select city0_.id as id1_1_0_, city0_.title as title2_1_0_ from city city0_ where city0_.id=?
Hibernate: select street0_.id as id1_3_0_, street0_.title as title2_3_0_ from street street0_ where street0_.id=?
Hibernate: select customers0_.address_id as address_4_2_1_, customers0_.id as id1_2_1_, customers0_.id as id1_2_0_, customers0_.address_id as address_4_2_0_, customers0_.first_name as first_na2_2_0_, customers0_.last_name as last_nam3_2_0_ from customer customers0_ where customers0_.address_id in (select address0_.id from address address0_ left outer join city city1_ on address0_.city_id=city1_.id left outer join street street2_ on address0_.street_id=street2_.id)
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     :  - customer Jack with that address 
SORTLOG     : end addresses with findAll()
```