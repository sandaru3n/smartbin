# Foreign Key Constraint Fix - DataInitializer

## 🐛 Error Description

### **Error Message:**
```
ERROR: update or delete on table "users" violates foreign key constraint "fkcr9i7wnmerwrsrooovfud772m" on table "bin_assignments"
Detail: Key (id)=(520) is still referenced from table "bin_assignments".
```

### **Root Cause:**
The `DataInitializer` was trying to delete users from the database, but the new `bin_assignments` table has foreign key constraints referencing the `users` table. Since bin assignments exist that reference users, the database prevents the deletion to maintain referential integrity.

### **Location:**
`src/main/java/com/sliit/smartbin/smartbin/config/DataInitializer.java` - line 53 (previously)

## ✅ Solution Applied

### **Fix: Delete in Correct Order**

#### **Before (Incorrect Order):**
```java
routeBinRepository.deleteAll();
collectionRepository.deleteAll();
routeRepository.deleteAll();
binRepository.deleteAll();
userRepository.deleteAll(); // ❌ ERROR: users still referenced by bin_assignments
```

#### **After (Correct Order):**
```java
binAssignmentRepository.deleteAll(); // ✅ Delete bin assignments first
routeBinRepository.deleteAll();
collectionRepository.deleteAll();
routeRepository.deleteAll();
binRepository.deleteAll();
userRepository.deleteAll(); // ✅ Now safe to delete users
```

### **Explanation:**
The deletion order must follow the dependency chain:
1. **bin_assignments** - References users (collector_id, assigned_by_id)
2. **route_bins** - References routes and bins
3. **collections** - References bins, routes, and users
4. **routes** - References users
5. **bins** - No foreign keys to other tables
6. **users** - Can be deleted last after all references removed

## 🔧 Changes Made

### **File Modified:**
`src/main/java/com/sliit/smartbin/smartbin/config/DataInitializer.java`

### **Changes:**
1. **Import Added:**
   ```java
   import com.sliit.smartbin.smartbin.repository.BinAssignmentRepository;
   ```

2. **Field Added:**
   ```java
   private final BinAssignmentRepository binAssignmentRepository;
   ```

3. **Constructor Updated:**
   ```java
   public DataInitializer(UserRepository userRepository,
                          BinRepository binRepository,
                          CollectionRepository collectionRepository,
                          RouteRepository routeRepository,
                          RouteBinRepository routeBinRepository,
                          BinAssignmentRepository binAssignmentRepository) {
       // ... assignments ...
       this.binAssignmentRepository = binAssignmentRepository;
   }
   ```

4. **Deletion Order Updated:**
   ```java
   binAssignmentRepository.deleteAll(); // Added as first deletion
   routeBinRepository.deleteAll();
   collectionRepository.deleteAll();
   routeRepository.deleteAll();
   binRepository.deleteAll();
   userRepository.deleteAll();
   ```

## 📊 Dependency Chain

```
bin_assignments
    ├─→ users (collector_id FK)
    └─→ users (assigned_by_id FK)

route_bins
    ├─→ routes (route_id FK)
    └─→ bins (bin_id FK)

collections
    ├─→ bins (bin_id FK)
    ├─→ routes (route_id FK)
    └─→ users (collector_id FK)

routes
    ├─→ users (collector_id FK)
    └─→ users (created_by_id FK)

bins
    └─ (no foreign keys)

users
    └─ (no foreign keys)
```

### **Deletion Order (Top to Bottom):**
1. **bin_assignments** (has most dependencies)
2. **route_bins**
3. **collections**
4. **routes**
5. **bins**
6. **users** (has no dependencies)

## 🧪 Testing

### **Verify Fix:**
1. Stop the application if running
2. Restart the Spring Boot application
3. Watch console output

### **Expected Output:**
```
Initializing database with sample data...
Clearing existing data...
Creating sample users...
Created user: John Doe (RESIDENT)
Created user: Sarah Williams (RESIDENT)
...
Created user: David Collector (COLLECTOR)
...
✅ Started SmartbinApplication in X.XXX seconds
```

### **No Error Should Appear:**
❌ Should NOT see: `ERROR: update or delete on table "users" violates foreign key constraint`

## 🔍 Future Considerations

### **Database Cascade Options**

#### **Option 1: ON DELETE CASCADE**
Configure foreign keys to automatically delete child records:
```java
@ManyToOne
@JoinColumn(name = "collector_id", nullable = false)
@OnDelete(action = OnDeleteAction.CASCADE)
private User collector;
```

**Pros:**
- Automatic cleanup
- No need to manually delete child records

**Cons:**
- Data loss if user accidentally deleted
- Less control over deletion

#### **Option 2: Manual Deletion (Current)**
Delete child records before parent records (current implementation)

**Pros:**
- Full control over deletion
- Prevents accidental data loss
- Clear deletion order

**Cons:**
- Must maintain correct deletion order
- More code to manage

### **Recommendation:**
Keep current manual deletion approach for better control and data safety.

## ✅ Summary

The foreign key constraint error has been fixed by:
- ✅ Adding `BinAssignmentRepository` to `DataInitializer`
- ✅ Deleting bin assignments before users
- ✅ Following proper dependency chain
- ✅ Maintaining referential integrity

The application should now start successfully without constraint violations! 🎉

## 📋 Checklist

Before running the application:
- [x] BinAssignmentRepository imported
- [x] binAssignmentRepository field added
- [x] Constructor updated with repository
- [x] binAssignmentRepository.deleteAll() called first
- [x] Deletion order follows dependency chain
- [x] No linter errors

The fix is complete and the application is ready to run! ✅

