# Invitation app - Broccoli & Co.
My app for test task for the position of a Junior Android Developer.


## What does the app do?
The application works with the server.
<br>
It allows users to enter their name and e-mail to receive e-mail invitations to the service.
<br>
If a user has already requested an invitation, the page says they are already registered and allows them to cancel the invitation.
<br>
The user can close the application and his data will be saved.
<br>
If the server returns an error (it has one hardcoded e-mail usedemail@blinq.app), the application will report that the e-mail has already been used.

⚠ Since this is a test task, the server may stop working.


## Concepts, libraries, and features used
- [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) architecture: to divide the app into layers
- [Retrofit](https://square.github.io/retrofit/) library: for easy network interactions
- Coroutines: for UI continuity during network interactions
- ViewModel, LiveData and ViewBinding
- SharedPreferences: to save the user's state
- AlertDialogs


<p>
  <img src="https://user-images.githubusercontent.com/81878781/214561406-cbb3ae04-4f15-456f-99fa-4a6e25a157da.png" width="16%">
  <img src="https://user-images.githubusercontent.com/81878781/214881329-8f46e164-0658-4b71-abd5-ba89d94984ed.png" width="16%">
  <img src="https://user-images.githubusercontent.com/81878781/214561857-1472898d-7dc7-41c9-8c87-fcde8bb21169.png" width="16%">
  <img src="https://user-images.githubusercontent.com/81878781/214561879-64bdcf01-b248-49f3-a717-c1a9529f9c7a.png" width="16%">
  <img src="https://user-images.githubusercontent.com/81878781/214561905-56c03689-f443-4a7b-b650-687ddf45f077.png" width="16%">
  <img src="https://user-images.githubusercontent.com/81878781/214561920-10d1a9b4-377c-48e8-9f0f-2cb0fad43b62.png" width="16%">
</p>
