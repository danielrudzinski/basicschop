package pl.myproject.basicshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import pl.myproject.basicshop.dto.OrderItemsDTO;
import pl.myproject.basicshop.dto.UsersDTO;
import pl.myproject.basicshop.mapper.OrderItemsMapper;
import pl.myproject.basicshop.mapper.UsersMapper;
import pl.myproject.basicshop.model.Users;
import pl.myproject.basicshop.repository.UsersRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final OrderItemsMapper orderItemsMapper;

    @Autowired
    public UsersService(UsersRepository usersRepository, UsersMapper usersMapper, OrderItemsMapper orderItemsMapper) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.orderItemsMapper = orderItemsMapper;
    }

    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        List<UsersDTO> usersDTOs = StreamSupport.stream(usersRepository.findAll().spliterator(),false)
                .map(usersMapper)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usersDTOs);
    }

    public ResponseEntity<UsersDTO> getUserById(@PathVariable Integer id) {
        return usersRepository.findById(id)
                .map(usersMapper::apply)  // mapujemy Users na UsersDTO
                .map(ResponseEntity::ok)  // opakowanie w ResponseEntity
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    public ResponseEntity<Users> addUsers(@RequestBody Users users){
        Users savedUsers = usersRepository.save(users);
        UriComponents uriComponents = ServletUriComponentsBuilder.
                fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUsers.getId());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }
    public ResponseEntity<Void>deleteUser(@PathVariable Integer id ){
        if(!usersRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        usersRepository.deleteById(id);
        return ResponseEntity.noContent().build();

    }
    public ResponseEntity<Users> updateUser(@PathVariable Integer id, @RequestBody Users user ){
        return   usersRepository.findById(id)
                .map(existingUser ->{
                    existingUser.setEmail(user.getEmail());
                    existingUser.setPassword(user.getPassword());

                    return usersRepository.save(existingUser);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    public ResponseEntity<Users>patchUser(@PathVariable Integer id, @RequestBody Users user ){
        return   usersRepository.findById(id)
                .map(existingUser ->{
                    if(existingUser.getEmail()!=null) existingUser.setEmail(user.getEmail());
                    if(existingUser.getPassword()!=null) existingUser.setPassword(user.getPassword());

                    return usersRepository.save(existingUser);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
