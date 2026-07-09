The spring boot appliction uses MVC and REST Controllers. Thymeleaf controllers are used for admin and doctor dashboard, while Rest Controllers serve all other modules.The appication interacts with two databases MySQL (for patient, doctor, appointment, and admin data) and MongoDB(for precription). All controllers route requests through a common service layer, which in turn delegtes to approprite JPA Entities.

Numbered flow of data and control

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate thymeleaf or REST controller.
3. The controller call the services layer to route to appropriate repositories
4. For relational date, the service layer calls the MySQL repositories. If the document data like prescription, the layer calls MongoDB repositories.
5. The repositories MySQL or MongoDB directly interact with the physical databases.
6. The data retrieved from the databases is mapped into the core repo model structure.
7. These models are defined by specific JPA Entities representing core domain objects.
